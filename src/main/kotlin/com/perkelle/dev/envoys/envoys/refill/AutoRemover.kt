package com.perkelle.dev.envoys.envoys.refill

import com.perkelle.dev.envoys.Envoys
import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.envoys.EnvoyManager
import com.perkelle.dev.envoys.utils.runLater
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Chest

class AutoRemover(val envoy: Envoy) {

    private val envoyManager = EnvoyManager()

    fun run(removeTime: Long = getConfig().getGeneric("envoy-auto-remove.remove-time", 120) * 20L) {
        runLater(removeTime) {
            envoy.hologram?.remove()

            val chest = envoy.location.block.state as? Chest // Could have already been broken
            if(chest != null) {
                // Clear the chest first to stop items spewing out
                chest.inventory.clear()
                chest.blockInventory.clear()

                // Give time for the chest to clear, then remove it
                Bukkit.getScheduler().runTaskLater(Envoys.instance.pl, Runnable {
                    chest.block.type = Material.AIR
                }, 4L)

                // Remove the envoy internally
                envoyManager.removeActiveEnvoy(envoy)
                envoyManager.removeTime(envoy)
                envoyManager.removeOpened(envoy)
            }
        }
    }
}