package com.perkelle.dev.envoys.commands.executors

import com.perkelle.dev.envoys.inventories.editMainMenu
import com.perkelle.dev.envoys.utils.command.CommandContext
import com.perkelle.dev.envoys.utils.command.Executor

class EditCommand: Executor {

    override fun CommandContext.onExecute() {
        player!!.openInventory(editMainMenu)
    }
}