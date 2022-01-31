package com.perkelle.dev.envoys.commands

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.perkelle.dev.envoys.commands.executors.*
import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.config.sendFormatted
import com.perkelle.dev.envoys.envoys.items.tiers.TierManager
import com.perkelle.dev.envoys.utils.command.CommandContext
import com.perkelle.dev.envoys.utils.command.Executor
import com.perkelle.dev.envoys.utils.command.ICommand
import com.perkelle.dev.envoys.utils.command.command
import me.lucko.commodore.CommodoreProvider
import org.bukkit.Bukkit

class EnvoysCommand: ICommand {

    private val tierManager = TierManager()

    private val root: String
        get() = getConfig().getGeneric("root-command", "/envoys").removePrefix("/")

    override fun register() {
        command(root) {
            aliases("envoy")

            if(CommodoreProvider.isSupported()) {
                commandNode = getCommandNode(this@EnvoysCommand.root, tierManager)
            }

            root(false, "envoys.help", object: Executor {
                override fun CommandContext.onExecute() {
                    sender sendFormatted "-= Envoys =-"
                    sender sendFormatted "Incorrect usage."
                    sender sendFormatted "All commands:"

                    subCommands.forEach { (meta, _) ->
                        sender sendFormatted "/envoys ${meta.name} | ${meta.description}"
                    }
                }
            })

            subCommand("additem", true, "envoys.additem", "Adds the current item in your hand to the contents list in the config", executor = AddItemCommand())
            subCommand("amount", true, "envoys.amount", "Displays how many envoys there are, in the whole server and the world", executor = AmountCommand())
            subCommand("clearholograms", true, "envoys.clearholograms", "Deletes all holograms", executor = ClearHologramsCommand())
            subCommand("compass", true, "envoys.compass", "Points your compass to the nearest envoy", executor = CompassCommand())
            subCommand("create", true, "envoys.create", "Creates an envoy at your current location", executor = CreateCommand())
            subCommand("current", false, "envoys.currentenvoys", "Shows all the envoys that are currently unlooted", executor = CurrentEnvoysCommand())
            subCommand("debug", false, "envoys.debug", "Uploads envoys debug info to pastebin", executor = DebugCommand())
            subCommand("edit", true, "envoys.edit", "Opens the envoy editing GUI", executor = EditCommand())
            subCommand("giveflare", false, "envoys.giveflare", "Give an airdrop flare to a player", executor = GiveFlareCommand())
            subCommand("list", false, "envoys.list", "Lists the locations of all envoys", executor = ListCommand())
            subCommand("refill", false, "envoys.refill", "Forces an envoy refill", executor = RefillCommand())
            subCommand("reload", false, "envoys.reload", "Reloads the config", executor = ReloadCommand())
            subCommand("remove", false, "envoys.remove", "Removes a predefined envoy", executor = RemoveCommand())
            subCommand("savedata", false, "envoys.savedata", "Saves the data.yml", executor = SaveDataCommand())
            subCommand("single", false, "envoys.single", "Spawns a single envoy", executor = SingleCommand())
            subCommand("stop", false, "envoys.stop", "Stops the current envoy event and clears Envoy chests", "clear", executor = StopCommand())

            //subCommand("printnbt", true, "", "", executor = PrintNBTCommand())
        }
    }
}