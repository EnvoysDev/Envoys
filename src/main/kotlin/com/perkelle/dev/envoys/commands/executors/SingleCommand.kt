package com.perkelle.dev.envoys.commands.executors

import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.config.sendFormatted
import com.perkelle.dev.envoys.envoys.EnvoyManager
import com.perkelle.dev.envoys.envoys.items.tiers.TierManager
import com.perkelle.dev.envoys.envoys.refill.AutoRemover
import com.perkelle.dev.envoys.envoys.refill.Envoy
import com.perkelle.dev.envoys.events.EnvoySpawnEvent
import com.perkelle.dev.envoys.utils.command.CommandContext
import com.perkelle.dev.envoys.utils.command.Executor
import com.perkelle.dev.envoys.utils.runLater
import org.bukkit.Bukkit
import org.bukkit.Location

class SingleCommand : Executor {

    private val tierManager = TierManager()

    override fun CommandContext.onExecute() {
        if (args.size < 5) {
            sendSyntax()
            return
        }

        val world = Bukkit.getWorld(args[0])
        if (world == null) {
            sender.sendFormatted("World does not exist")
            return
        }

        val x = args[1].toIntOrNull()
        val y = args[2].toIntOrNull()
        val z = args[3].toIntOrNull()
        if (x == null || y == null || z == null) {
            sender.sendFormatted("x, y and z coordinated must be integers")
            return
        }

        val tier = tierManager.getTierByName(args[4])
        if (tier == null) {
            sender.sendFormatted("Invalid tier")
            return
        }

        var deleteAfter: Int? = null
        if (args.size >= 6) {
            deleteAfter = args[5].toIntOrNull()
            if (deleteAfter == null || deleteAfter <= 0) {
                sender.sendFormatted("Delete after must be a number in seconds")
                return
            }
        }

        val location = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
        val envoy = Envoy(tier, false, location)
        envoy.spawn()

        if (deleteAfter != null) {
            AutoRemover(envoy).run(deleteAfter * 20L)
        }
    }

    private fun CommandContext.sendSyntax() {
        sender.sendFormatted("Invalid syntax.")
        sender.sendFormatted("Usage: /envoys single [world] [x] [y] [z] [tier] <delete after seconds>")
    }
}