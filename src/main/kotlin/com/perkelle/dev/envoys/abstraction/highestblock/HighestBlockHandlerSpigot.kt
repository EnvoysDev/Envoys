package com.perkelle.dev.envoys.abstraction.highestblock

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.ArmorStand

class HighestBlockHandlerSpigot : HighestBlockHandler {
    override fun getHighestBlockAt(location: Location) = location.world.getHighestBlockAt(location)
}