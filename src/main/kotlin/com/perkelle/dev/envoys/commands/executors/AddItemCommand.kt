package com.perkelle.dev.envoys.commands.executors

import com.perkelle.dev.envoys.config.MessageType
import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.config.sendMessage
import com.perkelle.dev.envoys.utils.command.CommandContext
import com.perkelle.dev.envoys.utils.command.Executor
import com.perkelle.dev.envoys.utils.saveItem
import org.bukkit.Material

class AddItemCommand: Executor {

    override fun CommandContext.onExecute() {
        if(player?.itemInHand == null || player.itemInHand.type == Material.AIR) {
            sender sendMessage MessageType.NO_ITEM_IN_HAND
            return
        }

        val item = player.itemInHand

        if(args.size < 2) {
            sender sendMessage MessageType.NO_NAME_OR_CHANCE
            return
        }

        val name = args[0]
        val chance = args[1].toDoubleOrNull()

        if(chance == null) {
            sender sendMessage MessageType.NO_NAME_OR_CHANCE
            return
        }

        item.saveItem(name, chance)
        getConfig().save()

        sender sendMessage MessageType.ADDED_ITEM
    }
}