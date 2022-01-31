package com.perkelle.dev.envoys.listeners

import com.perkelle.dev.envoys.envoys.EnvoyManager
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class BreakListener : Listener {

    private val envoyManager = EnvoyManager()

    @EventHandler(priority = EventPriority.HIGH)
    fun onBreak(e: BlockBreakEvent) {
        val block = e.block

        if (block.type != Material.CHEST) return
        val chest = (block.state as? Chest)?.inventory?.holder ?: return

        if (envoyManager.getAllActiveEnvoys().none { it.location == block.location }) return

        val envoy = envoyManager.getActiveEnvoyAtLocation(block.location) ?: return
        envoyManager.removeActiveEnvoy(envoy)

        envoy.hologram?.remove()

        if (envoyManager.getAllActiveEnvoys().isEmpty()) {
            broadcastAllOpened(envoyManager)
        }
    }
}