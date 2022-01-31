package com.perkelle.dev.envoys.listeners

import com.perkelle.dev.envoys.config.*
import com.perkelle.dev.envoys.envoys.EnvoyManager
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import kotlin.math.roundToInt

class CompassClickListener : Listener {

    private val envoyManager = EnvoyManager()

    @EventHandler fun onClick(e: PlayerInteractEvent) {
        val p = e.player

        if (e.action != Action.RIGHT_CLICK_BLOCK && e.action != Action.RIGHT_CLICK_AIR) {
            return
        }

        if(p.hasPermission("envoys.compass") && p.itemInHand.type == Material.COMPASS) {
            if(getConfig().getGeneric("compass.always-on", false) || getData().getList("compass").contains(p.uniqueId.toString())) {
                val closest = envoyManager.findNearestEnvoy(p.location)
                if(closest == null) {
                    p sendMessage MessageType.COMPASS_NO_ACTIVE_ENVOYS
                } else {
                    val distance = closest.location.distance(p.location).roundToInt()
                    p send MessageType.COMPASS_ENVOY_DISTANCE.getMessage("%distance" to distance.toString())
                }
            }
        }
    }
}