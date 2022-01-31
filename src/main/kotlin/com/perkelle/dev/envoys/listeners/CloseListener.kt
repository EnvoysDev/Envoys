package com.perkelle.dev.envoys.listeners

import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.envoys.EnvoyManager
import com.perkelle.dev.envoys.utils.runLater
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class CloseListener: Listener {

    private val envoyManager = EnvoyManager()

    @EventHandler fun onClose(e: InventoryCloseEvent) {
        val chest = e.inventory.holder as? Chest ?: return
        if(envoyManager.getAllActiveEnvoys().none { it.location == chest.location }) return

        val envoy = envoyManager.getActiveEnvoyAtLocation(chest.location) ?: return

        if(getConfig().getGeneric("delete-after-open", true)) {
            envoyManager.removeActiveEnvoy(envoy)

            chest.blockInventory.clear()
            chest.inventory.clear()

            runLater(1L) {
                chest.block.type = Material.AIR
            }
        }

        if(envoyManager.getAllActiveEnvoys().isEmpty()) {
            broadcastAllOpened(envoyManager)
        }
    }
}