package com.perkelle.dev.envoys.commands.executors

import com.perkelle.dev.envoys.utils.command.CommandContext
import com.perkelle.dev.envoys.utils.command.Executor
import de.tr7zw.nbtapi.NBTItem

class PrintNBTCommand : Executor {
    override fun CommandContext.onExecute() {
        val nbt = NBTItem(player!!.inventory.itemInMainHand)
        println(nbt.asNBTString())
    }
}