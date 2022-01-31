package com.perkelle.dev.envoys.commands.executors

import com.perkelle.dev.envoys.config.*
import com.perkelle.dev.envoys.envoys.EnvoyManager
import com.perkelle.dev.envoys.utils.command.CommandContext
import com.perkelle.dev.envoys.utils.command.Executor

class ListCommand: Executor {

    private val envoyManager = EnvoyManager()

    override fun CommandContext.onExecute() {
        if(envoyManager.getPredefinedEnvoys().isEmpty()) {
            sender sendMessage MessageType.LIST_EMPTY
        } else {
            envoyManager.getPredefinedEnvoys().forEach {
                sender send MessageType.LIST.getMessage(
                        "%id" to it.id,
                        "%world" to (it.location.world?.name ?: "null"),
                        "%x" to it.location.blockX,
                        "%y" to it.location.blockY,
                        "%z" to it.location.blockZ,
                        "%tier" to it.tier.name
                )
            }
        }
    }
}