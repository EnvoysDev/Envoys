package com.perkelle.dev.envoys.commands.executors

import com.perkelle.dev.envoys.config.MessageType
import com.perkelle.dev.envoys.config.getMessage
import com.perkelle.dev.envoys.config.sendMessage
import com.perkelle.dev.envoys.envoys.EnvoyManager
import com.perkelle.dev.envoys.utils.command.CommandContext
import com.perkelle.dev.envoys.utils.command.Executor

class CurrentEnvoysCommand: Executor {

    private val envoyManager = EnvoyManager()

    override fun CommandContext.onExecute() {
        sender sendMessage  MessageType.CURRENT_ENVOYS_TOP

        envoyManager.getAllActiveEnvoys().forEach {
            sender.sendMessage(MessageType.CURRENT_ENVOYS_LINE.getMessage(
                    "%tier" to it.tier.name,
                    "%world" to (it.location.world?.name ?: "null"),
                    "%x" to it.location.blockX,
                    "%y" to it.location.blockY,
                    "%z" to it.location.blockZ
            ) ?: "")
        }
    }
}