package com.perkelle.dev.envoys.utils.command

interface Executor {

    fun CommandContext.onExecute()

    fun execute(commandContext: CommandContext) = commandContext.onExecute()
}