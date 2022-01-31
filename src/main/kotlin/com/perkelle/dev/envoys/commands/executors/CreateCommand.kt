package com.perkelle.dev.envoys.commands.executors

import com.perkelle.dev.envoys.config.MessageType
import com.perkelle.dev.envoys.config.getData
import com.perkelle.dev.envoys.config.sendFormatted
import com.perkelle.dev.envoys.config.sendMessage
import com.perkelle.dev.envoys.envoys.EnvoyManager
import com.perkelle.dev.envoys.envoys.items.tiers.TierManager
import com.perkelle.dev.envoys.envoys.refill.PredefinedEnvoy
import com.perkelle.dev.envoys.utils.command.CommandContext
import com.perkelle.dev.envoys.utils.command.Executor
import org.bukkit.Location

class CreateCommand : Executor {

    private val envoyManager = EnvoyManager()
    private val tierManager = TierManager()

    override fun CommandContext.onExecute() {
        if (args.isEmpty()) {
            sender sendFormatted "Usage: /envoy create [tier] <chance of spawning>"
            return
        }

        val tierName = args[0]
        val tier = tierManager.getTierByName(tierName)

        if (tier == null) {
            sender sendFormatted "Invalid tier. You can find the tiers in the contents.tiers section of your config.yml, or in the /envoys edit menu"
            return
        }

        // Get chance and ensure it is in the range [1-100]
        val chance = args.getOrNull(1)
                ?.toIntOrNull()
                ?.coerceAtLeast(0)
                ?.coerceAtMost(100) ?: 100

        val p = player!!

        val envoyLoc = Location(p.world, p.location.blockX.toDouble(), p.location.blockY.toDouble(), p.location.blockZ.toDouble())
        envoyManager.addPredefinedEnvoy(PredefinedEnvoy(envoyManager.getNextID(), tier, false, envoyLoc, chance))

        sender sendMessage MessageType.SETUP_FINISHED
        getData().save()
    }
}