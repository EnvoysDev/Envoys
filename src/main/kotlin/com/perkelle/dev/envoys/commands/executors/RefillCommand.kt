package com.perkelle.dev.envoys.commands.executors

import com.perkelle.dev.envoys.envoys.refill.RefillManager
import com.perkelle.dev.envoys.utils.command.CommandContext
import com.perkelle.dev.envoys.utils.command.Executor

class RefillCommand: Executor {

    private val refillManager = RefillManager()

    override fun CommandContext.onExecute() {
        refillManager.refill()
    }
}