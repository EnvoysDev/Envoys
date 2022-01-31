package com.perkelle.dev.envoys.commands.executors

import com.perkelle.dev.envoys.config.MessageType
import com.perkelle.dev.envoys.config.getData
import com.perkelle.dev.envoys.config.sendMessage
import com.perkelle.dev.envoys.envoys.EnvoyManager
import com.perkelle.dev.envoys.utils.command.CommandContext
import com.perkelle.dev.envoys.utils.command.Executor

class RemoveCommand: Executor {

    private val envoyManager = EnvoyManager()

    override fun CommandContext.onExecute() {
        if(args.isEmpty()) {
            sender sendMessage MessageType.ID_INVALID
            return
        }

        val id = args[0].toIntOrNull()
        if(id == null) {
            sender sendMessage MessageType.ID_INVALID
            return
        }

        val envoy = envoyManager.getPredefinedEnvoyByID(id)
        if(envoy == null) {
            sender sendMessage MessageType.ID_INVALID
            return
        }

        envoyManager.removePredefinedEnvoy(envoy)
        sender sendMessage MessageType.REMOVED_ENVOY
        getData().save()
    }
}