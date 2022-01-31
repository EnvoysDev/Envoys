package com.perkelle.dev.envoys.commands.executors

import com.perkelle.dev.envoys.config.MessageType
import com.perkelle.dev.envoys.config.getData
import com.perkelle.dev.envoys.config.sendMessage
import com.perkelle.dev.envoys.envoys.EnvoyManager
import com.perkelle.dev.envoys.utils.command.CommandContext
import com.perkelle.dev.envoys.utils.command.Executor
import org.bukkit.Material
import java.util.*

class CompassCommand : Executor {

    private val envoyManager = EnvoyManager()

    override fun CommandContext.onExecute() {
        // Update data.yml for persistence
        val enabled = getData().getList("compass").mapNotNull(UUID::fromString).toMutableList()

        if(enabled.contains(player!!.uniqueId)) {
            enabled.remove(player.uniqueId)
            player sendMessage MessageType.COMPASS_DISABLED
        } else {
            enabled.add(player.uniqueId)
            player sendMessage MessageType.COMPASS_ENABLED
        }

        getData().config.set("compass", enabled.map(UUID::toString))

        if(player.itemInHand.type == Material.COMPASS) {
            envoyManager.findNearestEnvoy(player.location)?.location?.let(player::setCompassTarget)
        }
    }
}