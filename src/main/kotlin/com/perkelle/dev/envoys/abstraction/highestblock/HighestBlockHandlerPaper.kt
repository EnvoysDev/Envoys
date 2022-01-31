package com.perkelle.dev.envoys.abstraction.highestblock

import org.bukkit.HeightMap
import org.bukkit.Location
class HighestBlockHandlerPaper : HighestBlockHandler {
    override fun getHighestBlockAt(location: Location) = location.world.getHighestBlockAt(location, HeightMap.MOTION_BLOCKING)
}