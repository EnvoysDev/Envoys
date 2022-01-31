package com.perkelle.dev.envoys.commands.executors

import com.perkelle.dev.envoys.config.MessageType
import com.perkelle.dev.envoys.config.getData
import com.perkelle.dev.envoys.config.sendMessage
import com.perkelle.dev.envoys.utils.command.CommandContext
import com.perkelle.dev.envoys.utils.command.Executor

class SaveDataCommand: Executor {

    override fun CommandContext.onExecute() {
        getData().save()
        sender sendMessage MessageType.DATA_SAVED
    }
}