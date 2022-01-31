package com.perkelle.dev.envoys.commands.executors

import com.perkelle.dev.envoys.config.MessageType
import com.perkelle.dev.envoys.config.getMessage
import com.perkelle.dev.envoys.config.send
import com.perkelle.dev.envoys.config.sendFormatted
import com.perkelle.dev.envoys.envoys.EnvoyManager
import com.perkelle.dev.envoys.utils.command.CommandContext
import com.perkelle.dev.envoys.utils.command.Executor

class AmountCommand: Executor {

    private val envoyManager = EnvoyManager()

    override fun CommandContext.onExecute() {
        sender send MessageType.AMOUNT.getMessage(
                "%amount" to envoyManager.getPredefinedEnvoys().size,
                "%worldAmount" to envoyManager.getPredefinedEnvoys().filter { it.location.world == player!!.world }.size
        )
    }
}