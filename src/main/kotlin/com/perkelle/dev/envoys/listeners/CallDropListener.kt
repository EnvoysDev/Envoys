package com.perkelle.dev.envoys.listeners

import com.perkelle.dev.envoys.config.*
import com.perkelle.dev.envoys.envoys.items.tiers.TierManager
import com.perkelle.dev.envoys.envoys.refill.Envoy
import com.perkelle.dev.envoys.events.EnvoySpawnEvent
import com.perkelle.dev.envoys.utils.nbt.Constants
import com.perkelle.dev.envoys.utils.runLater
import com.perkelle.dev.envoys.worldguard.WorldGuardUtil
import com.perkelle.dev.envoys.worldguard.getRegionByName
import com.perkelle.dev.envoys.worldguard.isInRegion
import de.tr7zw.nbtapi.NBTItem
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class CallDropListener: Listener {

    private val tierManager = TierManager()

    @EventHandler(priority = EventPriority.HIGHEST) fun onPlace(e: BlockPlaceEvent) {
        val p = e.player
        val item = e.itemInHand
        val block = e.blockPlaced

        if(!getConfig().getGeneric("drop-item.enabled", true)) return

        val nbt = NBTItem(item)
        if (nbt.getBoolean(Constants.KEY_IS_FLARE) != true) {
            return
        }

        if (getConfig().getList("drop-item.disabled-worlds").contains(p.world.name)) {
            p sendMessage MessageType.DISABLED_REGION
            e.isCancelled = true
            return
        }

        if(WorldGuardUtil.isInstalled()) {
            val regions = getConfig().getList("drop-item.disabled-regions").mapNotNull { p.world.getRegionByName(it) }
            if(regions.any { region -> block.location.isInRegion(region) }) {
                p sendMessage MessageType.DISABLED_REGION
                e.isCancelled = true
                return
            }
        }

        block.type = Material.AIR

        val stack = item.clone()
        stack.amount = 1
        p.inventory.remove(stack)

        val tierName = nbt.getString(Constants.KEY_TIER) ?: return
        val waitTime = getConfig().getGeneric("drop-item.wait-time", 0) * 20L
        val targets =
                if(getConfig().getGeneric("random-location.broadcast-locations.only-to-players-in-same-world", false)) p.world.players
                else Bukkit.getOnlinePlayers()

        targets.forEach {
            it send MessageType.ON_DROP_PLACE.getMessage(
                    "%player" to p.name,
                    "%x" to block.x.toString(),
                    "%y" to block.y.toString(),
                    "%z" to block.z.toString()
            )
        }

        p.sendMessage(MessageType.CALLED_DROP)
        runLater(waitTime) {
            val tier = tierManager.getTierByName(tierName) ?: return@runLater

            val envoy = Envoy(tier, false, block.location)
            if (getConfig().getGeneric("fall-from-sky.enabled", true)) {
                envoy.dropFromSky()
            } else { // Just call the envoy event without a falling block
                Bukkit.getServer().pluginManager.callEvent(EnvoySpawnEvent(envoy))
            }
        }
    }
}