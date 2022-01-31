package com.perkelle.dev.envoys.utils.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import com.perkelle.dev.envoys.Envoys
import com.perkelle.dev.envoys.config.*
import com.perkelle.dev.envoys.utils.registerCommand
import com.perkelle.dev.envoys.utils.with
import me.lucko.commodore.Commodore
import me.lucko.commodore.CommodoreProvider
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandBuilder(name: String): Command(name) {

    lateinit var root: Pair<CommandMeta, Executor>
    val subCommands = mutableMapOf<CommandMeta, Executor>()
    var commandNode: LiteralCommandNode<*>? = null

    override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
        if(args.isEmpty() || subCommands.none { it.key.name.equals(args[0], true) }) {
            val meta = root.first
            val executor = root.second

            if(meta.permission.isNotEmpty() && !sender.hasPermission(meta.permission)) {
                sender sendMessage MessageType.NO_PERMISSION
                return true
            }

            if(meta.playerOnly && sender !is Player) {
                sender sendFormatted "Only players can use this command"
                return true
            }

            executor.execute(CommandContext(sender, sender as? Player, label, args))
        } else {
            val subCommand = subCommands.entries.first { it.key.name.equals(args[0], true) }
            val meta = subCommand.key
            val executor = subCommand.value

            if(meta.permission.isNotEmpty() && !sender.hasPermission(meta.permission)) {
                sender send MessageType.NO_PERMISSION.getMessage()
                return true
            }

            if(meta.playerOnly && sender !is Player) {
                sender send "Only players can use this command"
                return true
            }

            executor.execute(CommandContext(sender, sender as? Player, meta.name, args.copyOfRange(1, args.size)))
        }

        return true
    }

    fun root(playerOnly: Boolean, permission: String, executor: Executor) {
        root = CommandMeta(name, playerOnly, permission, null) to executor
        registerCommand(this)
        registerBrigadier()
    }

    fun subCommand(argument: String, playerOnly: Boolean, permission: String, description: String, vararg subAliases: String, executor: Executor) {
        subCommands.putAll(subAliases.with(argument).map { CommandMeta(it, playerOnly, permission, description) to executor })
    }

    fun aliases(vararg aliases: String) {
        this.aliases = aliases.toList()
    }

    fun registerBrigadier() {
        if(CommodoreProvider.isSupported() && commandNode != null) {
            val commodore = CommodoreProvider.getCommodore(Envoys.instance.pl)
            commodore.register(this, commandNode)
        }
    }
}