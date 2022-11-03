package com.perkelle.dev.envoys

import com.perkelle.dev.envoys.abstraction.JavaVersion
import com.perkelle.dev.envoys.abstraction.ServerVersion
import com.perkelle.dev.envoys.commands.EnvoysCommand
import com.perkelle.dev.envoys.config.*
import com.perkelle.dev.envoys.envoys.EnvoyManager
import com.perkelle.dev.envoys.envoys.items.contents.ContentsManager
import com.perkelle.dev.envoys.envoys.items.tiers.TierManager
import com.perkelle.dev.envoys.envoys.refill.EnvoySpawnListener
import com.perkelle.dev.envoys.envoys.refill.RefillManager
import com.perkelle.dev.envoys.holograms.isHolographicDisplaysInstalled
import com.perkelle.dev.envoys.integrations.IntegrationProvider
import com.perkelle.dev.envoys.listeners.*
import com.perkelle.dev.envoys.utils.async
import com.perkelle.dev.envoys.utils.gui.GUIListener
import com.perkelle.dev.envoys.utils.sync
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.Method
import java.util.*
import javax.script.ScriptEngineManager

fun getEnvoysCore() = Envoys.instance

var verboseLogging = false
fun verboseLog(str: String) {
    if (verboseLogging) {
        Bukkit.getLogger().info(str)
    }
}

class Envoys(val pl: Plugin) {

    private var syncCommandsMethod: Method? = null

    companion object {
        lateinit var instance: Envoys

        @JvmStatic
        public var nextRefill = 0L
    }

    init {
        instance = this
    }

    lateinit var config: Config
    lateinit var data: Data

    fun run() {
        if (ServerVersion.version >= ServerVersion.V1_17) {
            this.start()
        } else {
            if (JavaVersion.version >= 16) {
                pl.logger.severe(
                    """
                    Envoys does not support Java 16 and higher on Minecraft versions lower than 1.17.
                    This is due to changes in class loading, for which Spigot offers new features, only available in
                    1.17 or higher. Please update your server to use Minecraft 1.17 or higher,
                    or downgrade the Java version you are using.
                    """.trimIndent()
                )
                return
            }

            this.start();
        }
    }

    fun onDisable() {
        data.save()
    }

    private fun start() {
        // DRM and stuff
        pl.server.scheduler.runTaskAsynchronously(pl, Runnable {
            bootstrap()
        })

        // Check for updates
        pl.server.scheduler.runTaskTimer(pl, Runnable {
            VersionManager.getLatestVersion { version ->
                if (version != null && version != pl.description.version) {
                    pl.logger.info(
                        "A new version of Envoys is available! It is recommended that you update ASAP.\n" +
                                "You are running: v${pl.description.version}\n" +
                                "Latest version: v$version"
                    )
                }
            }
        }, 0, VersionManager.CHECK_FREQUENCY.seconds * 20)

        async({ // async
            // Config
            config = Config()
            config.load()
            ConfigUpdater().update()

            verboseLogging = config.getGeneric("verbose-logging", false)
            verboseLog("Loaded config")

            // Load data
            data = Data()
            data.load()
            verboseLog("Loaded data")

            verboseLog("Registering listeners")
            listOf(
                    BreakListener(),
                    CallDropListener(),
                    ChatListener(),
                    CloseListener(),
                    CompassClickListener(),
                    EnvoySpawnListener(),
                    GUIListener(),
                    JoinListener(),
                    OpenListener(),
                    RefillAfterOpenListener()
            ).forEach { Bukkit.getServer().pluginManager.registerEvents(it, Envoys.instance.pl) }

            verboseLog("Registering tiers + items")
            ContentsManager().loadContents()
            TierManager().loadTiers()
        }) { // sync
            // Bstats
            if (config.getGeneric("enable-metrics", true)) {
                Metrics(pl as JavaPlugin, 5824)
                if (verboseLogging) Bukkit.getLogger().info("Initiated metrics")
            }

            if (config.getGeneric("holograms.enabled", true) && !isHolographicDisplaysInstalled()) {
                Bukkit.getLogger()
                    .warning("Holograms are enabled in the Envoys config, but HolographicDisplays is not installed")
            }

            // Load envoy data
            val envoyManager = EnvoyManager()
            envoyManager.loadPredefinedEnvoys()
            envoyManager.loadActiveEnvoys()
            verboseLog("Loaded active / predefined envoys")

            // Other integrations
            verboseLog("Loading integrations")
            IntegrationProvider.loadIntegrations(config)
            verboseLog("Integrations hooked")

            // Register commands
            EnvoysCommand().register()
            try {
                syncCommandsMethod = Bukkit.getServer().javaClass.getDeclaredMethod("syncCommands")
                syncCommandsMethod?.isAccessible = true
                syncCommandsMethod?.invoke(Bukkit.getServer())
            } catch (e: Exception) {
                verboseLog("Completely failed to register commands")
            }
            verboseLog("Registered commands")

            val refillManager = RefillManager()
            if (config.getGeneric("start-refill-countdown-after-opened", false)) {
                verboseLog("Starting initial refill")

                // Delay for a second otherwise the first envoy doesn't spawn
                sync(20) {
                    refillManager.refill() // Initial refill
                    verboseLog("Completed initial refill")
                }
            } else {
                // Start refill loop
                verboseLog("Starting refill loop")
                val refillDelay: Long
                val raw = config.config.get("envoy-refill-delay")
                refillDelay = if (raw is Int) {
                    raw * 20L
                } else {
                    (ScriptEngineManager().getEngineByName("js")
                        .eval(config.getGeneric("envoy-refill-delay", "1800")) as Int) * 20L
                }

                if (!config.getGeneric("envoys-refilled-by-command-only", false)) {
                    nextRefill = System.currentTimeMillis() + (refillDelay * 50)

                    val countdowns = config.getConfigurationSection("refill-countdown")?.getKeys(false)
                        ?.mapNotNull(String::toIntOrNull)
                        ?.map { it to config.getGeneric("refill-countdown.$it", "$it seconds until refill") }
                        ?: emptyList()

                    // Initial countdown
                    countdowns.forEach { (delay, message) ->
                        pl.server.scheduler.runTaskLater(pl, Runnable {
                            if (minimumThresholdMet()) {
                                Bukkit.getOnlinePlayers().forEach { it sendFormatted message }
                            }
                        }, refillDelay - (delay * 20L))
                    }

                    pl.server.scheduler.runTaskTimer(pl, Runnable {
                        nextRefill = System.currentTimeMillis() + (refillDelay * 50)

                        if (minimumThresholdMet()) {
                            refillManager.refill()

                            countdowns.forEach { (delay, message) ->
                                pl.server.scheduler.runTaskLater(pl, Runnable {
                                    Bukkit.getOnlinePlayers().forEach { it sendFormatted message }
                                }, refillDelay - (delay * 20L))
                            }
                        }
                    }, refillDelay, refillDelay)
                }
            }

            // Compass feature
            verboseLog("Starting compass hook")
            Bukkit.getScheduler().runTaskTimer(
                pl,
                Runnable {
                    val targets =
                        if (config.getGeneric("compass.always-on", false)) Bukkit.getOnlinePlayers()
                        else getData().getList("compass")
                            .mapNotNull(UUID::fromString)
                            .mapNotNull(Bukkit::getPlayer)

                    targets
                        .filter { it.hasPermission("envoys.compass") }
                        .filter { it.itemInHand.type == Material.COMPASS }
                        .forEach { p -> envoyManager.findNearestEnvoy(p.location)?.location?.let(p::setCompassTarget) }
                },
                config.getGeneric("compass.refresh-interval", 10) * 20L,
                config.getGeneric("compass.refresh-interval", 10) * 20L
            )
        }
    }
}

fun minimumThresholdMet() = !getConfig().getGeneric("envoy-refill-minimum-players.enabled", false) ||
        Bukkit.getOnlinePlayers().size >= getConfig().getGeneric("envoy-refill-minimum-players.minimum-players", 10)
