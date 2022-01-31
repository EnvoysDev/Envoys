package com.perkelle.dev.envoys.listeners

import com.perkelle.dev.envoys.Envoys
import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.envoys.EnvoyManager
import com.perkelle.dev.envoys.envoys.items.tiers.TierManager
import com.perkelle.dev.envoys.envoys.refill.Envoy
import com.perkelle.dev.envoys.envoys.refill.PredefinedEnvoy
import com.perkelle.dev.envoys.envoys.refill.RefillManager
import com.perkelle.dev.envoys.envoys.refill.random.RandomLocationEnvoyGenerator
import com.perkelle.dev.envoys.events.EnvoySpawnEvent
import io.papermc.lib.PaperLib
import org.bukkit.Bukkit
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent

class RefillAfterOpenListener : Listener {

    private val envoyManager = EnvoyManager()
    private val randomLocationGenerator = RandomLocationEnvoyGenerator()

    @EventHandler(priority = EventPriority.LOW)
    fun onOpen(e: InventoryOpenEvent) {
        val chest = e.inventory.holder as? Chest ?: return

        if (!getConfig().getGeneric("start-refill-countdown-after-opened", false)) return
        var envoy = envoyManager.getActiveEnvoyAtLocation(chest.location) ?: return

        val refillDelay = getConfig().getGeneric("envoy-refill-delay", 1800) * 20L
        Bukkit.getServer().scheduler.runTaskLater(Envoys.instance.pl, Runnable {
            // if random location, generate new envoy
            if (envoy.isRandomLocation) {
                envoy = randomLocationGenerator.generateRandomLocationEnvoys(1).getOrElse(0) { envoy }
            }

            // Spawn falling blocks
            if (getConfig().getGeneric("fall-from-sky.enabled", true)) {
                // force load chunk
                PaperLib.getChunkAtAsync(envoy.location, true).thenAccept { chunk ->
                    envoy.dropFromSky()
                }
            } else { // Just call the envoy event without a falling block
                Bukkit.getServer().pluginManager.callEvent(EnvoySpawnEvent(envoy))
            }
        }, refillDelay)
    }
}