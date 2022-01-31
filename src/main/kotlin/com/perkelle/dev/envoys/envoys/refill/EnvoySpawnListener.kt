package com.perkelle.dev.envoys.envoys.refill

import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.envoys.EnvoyManager
import com.perkelle.dev.envoys.envoys.items.contents.EnvoyItem
import com.perkelle.dev.envoys.events.EnvoySpawnEvent
import com.perkelle.dev.envoys.holograms.isHolographicDisplaysInstalled
import com.perkelle.dev.envoys.verboseLog
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.concurrent.ThreadLocalRandom

class EnvoySpawnListener : Listener {

    private val envoyManager = EnvoyManager()

    @EventHandler
    fun onSpawn(e: EnvoySpawnEvent) {
        verboseLog("Running spawn listener")

        val envoy = e.envoy
        var items = envoy.tier.generateItems()

        // Register envoy
        envoyManager.addActiveEnvoy(envoy)

        // Call the API event
        val apiEvent = com.perkelle.dev.envoys.api.EnvoySpawnEvent(envoy.location, envoy.tier, items, envoy.isRandomLocation)
        Bukkit.getServer().pluginManager.callEvent(apiEvent)
        items = apiEvent.items // Allow developers to update the items in an envoy

        if (apiEvent.isCancelled) return // We can stop execution here, the envoy hasn't been registered anywhere yet

        // Not sure why this exists, copying it over from the old refill manager
        envoyManager.removeOpened(envoy.location)

        // If we're not using falling blocks, we could end up overwriting an existing envoy. We want to clear it out first to stop a ton of loot amassing.
        if (envoy.location.block.type == Material.CHEST) {
            val chest = envoy.location.block.state as Chest
            chest.inventory.clear()
        }

        // For when we're not using falling blocks
        envoy.location.block.type = Material.CHEST
        val chest = envoy.location.block.state as Chest

        // Add the items to the envoy
        items.forEach { item ->
            val slot = ThreadLocalRandom.current().nextInt(0, 27)
            chest.inventory.setItem(slot, item.getAs<EnvoyItem>().getItemStack())
        }

        // Spawn a hologram if enabled
        if (getConfig().getGeneric("holograms.enabled", true) && isHolographicDisplaysInstalled()) {
            envoy.spawnHologram()
        }

        // Auto remove
        if (getConfig().getGeneric("envoy-auto-remove.enabled", false)) {
            AutoRemover(envoy).run()
        }

        // Run commands-on-spawn
        envoy.runCommandsOnSpawn()
    }
}