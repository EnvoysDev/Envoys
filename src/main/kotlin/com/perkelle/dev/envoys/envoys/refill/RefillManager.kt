package com.perkelle.dev.envoys.envoys.refill

import com.perkelle.dev.envoys.Envoys
import com.perkelle.dev.envoys.config.*
import com.perkelle.dev.envoys.envoys.EnvoyManager
import com.perkelle.dev.envoys.envoys.refill.random.RandomLocationEnvoyGenerator
import com.perkelle.dev.envoys.events.EnvoySpawnEvent
import com.perkelle.dev.envoys.holograms.HologramWrapper
import com.perkelle.dev.envoys.holograms.isHolographicDisplaysInstalled
import com.perkelle.dev.envoys.utils.assertInRange
import com.perkelle.dev.envoys.utils.runLater
import com.perkelle.dev.envoys.verboseLog
import io.papermc.lib.PaperLib
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Chest
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.metadata.FixedMetadataValue
import java.util.concurrent.ThreadLocalRandom
import kotlin.properties.Delegates

class RefillManager {

    private val envoyManager = EnvoyManager()

    public var cd94ec90364da372eb1a980c7ffb36e5654d7e2cd92cb810be418adb36fe2434 = "%%__USERID__%%"

    companion object {
        const val HOLOGRAM_X_OFFSET = 0.5
        const val HOLOGRAM_Y_OFFSET = 1.5
        const val HOLOGRAM_Z_OFFSET = 0.5
    }

    fun refill() {
        verboseLog("Starting refill")

        // Calculate how many envoys to refill per second
        val perSecond = getConfig().getGeneric("updates-per-second", 5).assertInRange(0, 20, 5)
        val tickWait = 20 / perSecond

        // Create a list of locations:
        val envoys = mutableListOf<Envoy>()

        // Add predefined location envoys to the list of locations
        if (!getConfig().getGeneric("random-location.only-refill-random-location-envoys", false)) {
            addPredefinedEnvoys(envoys)
        }

        // Add random envoys to the list of locations
        if (getConfig().getGeneric("random-location.enabled", false)) {
            val random = RandomLocationEnvoyGenerator().generateRandomLocationEnvoys()
            envoys.addAll(random)
        }

        verboseLog("Generated locations: ${envoys.joinToString(", ") { it.location.toString() }}")

        // Delete old envoys
        if (getConfig().getGeneric("random-location.delete-old-envoys", true)) {
            deleteOldEnvoys()
        }

        verboseLog("Deleted old envoys, now dropping envoys")

        // Spawn envoys
        // TODO: What if the envoy lands on a torch and breaks?
        envoys.withIndex().forEach { (index, envoy) ->
            verboseLog("Started drop #$index")

            runLater(tickWait * index.toLong()) {
                verboseLog("Now dropping #$index")

                // Broadcast locations
                if (getConfig().getGeneric("random-location.broadcast-locations.enabled", false)) {
                    envoy.broadcastLocation()
                }

                PaperLib.getChunkAtAsync(envoy.location, true).thenAccept { chunk ->
                    // Spawn falling blocks
                    if (getConfig().getGeneric("fall-from-sky.enabled", true)) {
                        envoy.dropFromSky()
                    } else { // Just call the envoy event without a falling block
                        Bukkit.getServer().pluginManager.callEvent(EnvoySpawnEvent(envoy))
                    }
                }
            }
        }

        // Send players refilled message
        envoys.asSequence().mapNotNull(Envoy::location).mapNotNull(Location::getWorld).toSet().map(World::getPlayers).flatten().toList().forEach { p ->
            p sendMessage MessageType.REFILLED
        }
    }

    private fun addPredefinedEnvoys(envoys: MutableList<Envoy>) {
        val predefinedEnvoys = mutableListOf<PredefinedEnvoy>()

        if (getConfig().getGeneric("limit-predefined-envoys.enabled", false)) {
            val max = getConfig().getGeneric("limit-predefined-envoys.max", 10).coerceAtMost(envoyManager.getPredefinedEnvoys().size)
            val min = getConfig().getGeneric("limit-predefined-envoys.min", 0).coerceAtLeast(0).coerceAtMost(envoyManager.getPredefinedEnvoys().size)
            val amount = ThreadLocalRandom.current().nextInt(min, max + 1)

            val pool = mutableListOf(*envoyManager.getPredefinedEnvoys().toTypedArray())

            repeat(amount) {
                val index = ThreadLocalRandom.current().nextInt(0, pool.size)

                predefinedEnvoys.add(pool[index])
                pool.removeAt(index)
            }
        } else {
            predefinedEnvoys.addAll(envoyManager.getPredefinedEnvoys())
        }

        envoyManager.getPredefinedEnvoys().forEach { envoy ->
            if (ThreadLocalRandom.current().nextInt(0, 101) <= envoy.chance) { // Envoy spawn chance feature
                envoys.add(envoy)
            }
        }
    }

    private fun deleteOldEnvoys() {
        envoyManager.clearOpened()

        envoyManager.getAllActiveEnvoys().forEach { envoy ->
            // Clear chest out to prevent items dropping
            val chest = envoy.location.block.state as? Chest
            if (chest != null) {
                chest.inventory.clear()
                chest.blockInventory.clear()
            }

            // Delete chest block
            envoy.location.block.type = Material.AIR
        }

        // Clear out envoys from envoy manager
        envoyManager.clearActiveEnvoys()

        // Remove holograms
        if (isHolographicDisplaysInstalled()) HologramWrapper.clearHolograms()

        // Remove old times
        envoyManager.clearTimes()
    }
}