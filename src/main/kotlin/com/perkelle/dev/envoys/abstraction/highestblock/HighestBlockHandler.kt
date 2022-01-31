package com.perkelle.dev.envoys.abstraction.highestblock

import com.perkelle.dev.envoys.ServerType
import com.perkelle.dev.envoys.abstraction.ServerVersion
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.ArmorStand

interface HighestBlockHandler {

    fun getHighestBlockAt(location: Location): Block

    companion object {
        val instance: HighestBlockHandler by lazy {
            if (ServerVersion.version < ServerVersion.V1_13) {
                return@lazy HighestBlockHandlerSpigot()
            }

            when(ServerType.type) {
                ServerType.PAPER -> HighestBlockHandlerPaper()
                ServerType.SPIGOT -> HighestBlockHandlerSpigot()
            }
        }
    }
}