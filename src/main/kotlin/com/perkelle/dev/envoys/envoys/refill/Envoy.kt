package com.perkelle.dev.envoys.envoys.refill

import com.perkelle.dev.envoys.config.*
import com.perkelle.dev.envoys.envoys.items.tiers.Tier
import com.perkelle.dev.envoys.events.EnvoySpawnEvent
import com.perkelle.dev.envoys.holograms.HologramWrapper
import com.perkelle.dev.envoys.holograms.isHolographicDisplaysInstalled
import com.perkelle.dev.envoys.utils.translateColour
import com.perkelle.dev.envoys.verboseLog
import org.bukkit.Bukkit
import org.bukkit.Location

open class Envoy(val tier: Tier, val isRandomLocation: Boolean, val location: Location) {

    var hologram: HologramWrapper? = null

    fun runCommandsOnSpawn() {
        getConfig().getList("commands-on-spawn").forEach { command ->
            val formatted = command
                    .replace("%x", location.blockX.toString())
                    .replace("%y", location.blockY.toString())
                    .replace("%z", location.blockZ.toString())
                    .replace("%world", location.world?.name ?: "")
                    .replace("%tier", tier.name)
                    .translateColour()

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formatted)
        }
    }

    fun spawnHologram() {
        val hologramX = location.x + RefillManager.HOLOGRAM_X_OFFSET
        val hologramY = location.y + RefillManager.HOLOGRAM_Y_OFFSET
        val hologramZ = location.z + RefillManager.HOLOGRAM_Z_OFFSET
        val hologramLocation = Location(location.world, hologramX, hologramY, hologramZ)

        hologram = HologramWrapper(hologramLocation, tier.getHologramText())
        hologram?.spawn()
    }

    fun loadSavedHologram() {
        if (isHolographicDisplaysInstalled()) {
            val hologramX = location.x + RefillManager.HOLOGRAM_X_OFFSET
            val hologramY = location.y + RefillManager.HOLOGRAM_Y_OFFSET
            val hologramZ = location.z + RefillManager.HOLOGRAM_Z_OFFSET
            val encoded = getData().getList("holograms").find { it.startsWith("${location.world?.name};$hologramX;$hologramY;$hologramZ") }

            if (encoded != null) {
                hologram = HologramWrapper.decode(encoded)
                hologram?.spawn()
            } else {
                spawnHologram()
            }
        }
    }

    fun broadcastLocation() {
        // Calculate players to broadcast to - do we want all players of just the players in the same world?
        val players =
                if (getConfig().getGeneric("random-location.broadcast-locations.only-to-players-in-same-world", false)) location.world?.players
                        ?: emptyList()
                else Bukkit.getOnlinePlayers()

        players.forEach {
            it send MessageType.ENVOY_SPAWNED.getMessage(
                    "%world" to (location.world?.name ?: "null"),
                    "%x" to location.blockX,
                    "%y" to location.blockY,
                    "%z" to location.blockZ,
                    "%tier" to tier.name
            )
        }
    }

    fun dropFromSky() {
        verboseLog("Spawning armourstand at $location")

        val wrapper = ArmourStandWrapper(this)
        wrapper.spawn()
        wrapper.startLandCheckLoop()
    }

    fun spawn() {
        if (getConfig().getGeneric("fall-from-sky.enabled", true)) {
            dropFromSky()
        } else { // Just call the envoy event without a falling block
            Bukkit.getServer().pluginManager.callEvent(EnvoySpawnEvent(this))
        }
    }
}