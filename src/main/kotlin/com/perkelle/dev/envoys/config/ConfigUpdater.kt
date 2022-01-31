package com.perkelle.dev.envoys.config

import com.perkelle.dev.envoys.utils.Colours
import com.perkelle.dev.envoys.utils.config.YMLUtils
import org.bukkit.Bukkit
import java.util.*

class ConfigUpdater {

    private val changes = mapOf(
            40 to mapOf("lang.current-envoys.top" to "Current envoys:", "lang.current-envoys.line" to "There is an %tier envoy at: %world, %x, %y, %z"),
            41 to mapOf("tell-if-already-opened" to false, "lang.already-opened" to "This envoy was already opened %minutes minutes ago!"),
            42 to mapOf("start-refill-countdown-after-opened" to false),
            43 to mapOf(), // Reverted all changes from this one
            44 to mapOf("drop-item.broadcast-on-place" to "%player called an envoy in at %x, %y, %z", "drop-item.wait-time" to 0),
            45 to mapOf("envoy-refill-minimum-players.enabled" to false, "envoy-refill-minimum-players.minimum-players" to 10),
            46 to mapOf("envoy-auto-remove.enabled" to false, "envoy-auto-remove.remove-time" to 120),
            47 to mapOf(
                    "particles.enabled" to false,
                    "particles.style" to "circle",
                    "particles.particle-type" to "REDSTONE",
                    "particles.particle-speed" to 0.5,
                    "particles.colour.enabled" to true,
                    "particles.colour.colours" to Colours.values().map(Colours::name),
                    "particles.styles.circle.particle-count" to 10,
                    "particles.styles.circle.radius" to 2.0
            ),
            48 to mapOf(
                    "root-command" to "/envoys"
            ),
            49 to mapOf(
                    "drop-item.disabled-regions" to listOf("region1", "region2"),
                    "lang.disabled-region" to "Envoy drops cannot be called in this area"
            ),
            50 to mapOf(
                    "enable-metrics" to true
            ),
            51 to mapOf(
                    "compass.enabled" to true,
                    "compass.always-on" to false,
                    "compass.refresh-interval" to 10,
                    "lang.compass-disabled" to "Envoy compass tracking has been disabled",
                    "lang.compass-enabled" to "Envoy compass tracking has been enabled"
            ),
            52 to mapOf(
                    "lang.compass-envoy-distance" to "The nearest envoy is %distance metres away",
                    "lang.compass-no-active-envoys" to "There are no envoys currently active"
            ),
            53 to mapOf(
                    "verbose-logging" to false
            ),
            54 to mapOf(
                    "min-commands" to 0,
                    "max-commands" to 5
            ),
            55 to mapOf(
                    "commands-on-spawn" to listOf("broadcast &8>> &4A %tier envoy has spawned at %x %y %z in %world")
            ),
            56 to mapOf(
                    "random-location.minimum-y" to 1,
                    "random-location.maximum-y" to 255,
                    "random-location.custom-y" to null
            ),
            57 to mapOf(
                    "lang.cleared-envoys" to "Cleared all envoys",
                    "lang.all-envoys-opened" to "All envoys have been opened!",
                    "particles.particle-speed" to null,
                    "fall-from-sky.enabled" to true,
                    "fall-from-sky.y" to 255,
                    "fall-from-sky.check-interval" to 2
            ),
            58 to mapOf(),
            59 to mapOf(
                    "update-checker.notify-on-join" to true
            ),
            60 to mapOf(),
            61 to mapOf("random-locations.integrations.griefprevention.enabled" to false),
            62 to mapOf(
                    "lang.envoy-list-empty" to "You haven't created any predefined envoys yet. Use /envoy create to create some"
            ),
            63 to mapOf(
                    "fall-from-sky.use-alternative-on-ground-check" to false
            ),
            64 to mapOf(
                    "destroy-on-open.enabled" to false,
                    "destroy-on-open.transfer-items-to-inventory" to false
            ),
            65 to mapOf(
                    "random-location.dont-spawn-in-air" to false
            ),
            66 to mapOf(),
            67 to mapOf(
                    "limit-predefined-envoys.enabled" to false,
                    "limit-predefined-envoys.max" to 10,
                    "limit-predefined-envoys.min" to 0
            ),
            68 to mapOf(
                    "drop-item.disabled-worlds" to listOf("disabled-world-1", "disabled-world-2")
            )
    )

    private val customChanges = mapOf(
            58 to update58(),
            60 to update60(),
            66 to update66()
    )

    fun update() {
        var version = getConfig().getGenericOrNull<Int>("config-version")
        if (version == null) {
            Bukkit.getLogger().warning("-------------------------------------------------------")
            Bukkit.getLogger().warning("Your config-version field is malformed!")
            Bukkit.getLogger().warning("Envoys will not automatically update the config until this is corrected.")
            Bukkit.getLogger().warning("Discord support server: https://discord.gg/3Bv4FQn")
            Bukkit.getLogger().warning("-------------------------------------------------------")
            return
        }

        val latest = changes.keys.maxOrNull()!!
        val updated = version != latest

        while(version != latest) {
            version++

            changes[version]?.forEach { (key, value) ->
                getConfig().config.set(key, value)
            }

            customChanges[version]?.invoke()
        }

        if(updated) {
            getConfig().config.set("config-version", latest)
            getConfig().save()
            Bukkit.getLogger().info("Updated your envoys config")
        }
    }

    fun update58(): () -> Unit = {
        val whitelistedSection = getConfig().getConfigurationSection("random-location.worldguard-integration.whitelisted-regions")
        whitelistedSection?.getKeys(false)?.forEach { regionName ->
            val region = whitelistedSection.getConfigurationSection(regionName)
            region?.set("tier-selection", "automatic")
            region?.set("tiers", emptyMap<String, Int>())
        }
    }

    fun update60(): () -> Unit = {
        val whitelistedSection = getConfig().getConfigurationSection("random-location.worldguard-integration.whitelisted-regions")
        whitelistedSection?.getKeys(false)?.forEach { regionName ->
            whitelistedSection.set("$regionName.exclude", listOf("excluderegion1"))
        }
    }

    fun update66(): () -> Unit = {
        val text = getConfig().getGenericOrNull<String>("holograms.text")
        val new = text?.let(Collections::singletonList) ?: listOf("&b%tier Envoy", "&5line 2")

        val tiers = getConfig().getConfigurationSection("contents.tiers")
        tiers?.getKeys(false)?.mapNotNull(tiers::getConfigurationSection)?.forEach { tier ->
            tier.set("hologram-text", new)
        }
    }
}