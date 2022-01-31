package com.perkelle.dev.envoys.commands.executors

import com.perkelle.dev.envoys.config.MessageType
import com.perkelle.dev.envoys.config.sendFormatted
import com.perkelle.dev.envoys.config.sendMessage
import com.perkelle.dev.envoys.envoys.EnvoyManager
import com.perkelle.dev.envoys.holograms.HologramWrapper
import com.perkelle.dev.envoys.holograms.isHolographicDisplaysInstalled
import com.perkelle.dev.envoys.utils.command.CommandContext
import com.perkelle.dev.envoys.utils.command.Executor
import com.perkelle.dev.envoys.utils.runLater
import org.bukkit.Material
import org.bukkit.block.Chest

class StopCommand: Executor {

    private val envoyManager = EnvoyManager()

    override fun CommandContext.onExecute() {
        envoyManager.getAllActiveEnvoys().forEach { envoy ->
            val block = envoy.location.block
            val chest = block.state as? Chest ?: return@forEach

            chest.blockInventory.clear()
            chest.inventory.clear()

            runLater(1L) {
                block.type = Material.AIR
            }
        }

        envoyManager.clearActiveEnvoys()
        envoyManager.clearTimes()
        envoyManager.clearOpened()

        if(isHolographicDisplaysInstalled()) {
            HologramWrapper.clearHolograms()
        }

        sender sendMessage MessageType.CLEARED_ENVOYS
    }
}