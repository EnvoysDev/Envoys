package com.perkelle.dev.envoys.commands.executors

import com.perkelle.dev.envoys.abstraction.ServerVersion
import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.config.sendFormatted
import com.perkelle.dev.envoys.envoys.items.tiers.TierManager
import com.perkelle.dev.envoys.utils.XMaterial
import com.perkelle.dev.envoys.utils.command.CommandContext
import com.perkelle.dev.envoys.utils.command.Executor
import com.perkelle.dev.envoys.utils.itembuilder.item
import com.perkelle.dev.envoys.utils.nbt.Constants
import com.perkelle.dev.envoys.utils.translateColour
import de.tr7zw.nbtapi.NBTItem
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class GiveFlareCommand : Executor {

    private val tierManager = TierManager()

    override fun CommandContext.onExecute() {
        if (args.size < 2) {
            sender sendFormatted "You need to specify an online player, followed by a tier"
            return
        }

        val player = Bukkit.getPlayer(args[0])
        if (player == null) {
            sender sendFormatted "The player you specified is not online"
            return
        }

        val tierName = args[1]
        if (tierManager.getTierByName(tierName) == null) {
            sender sendFormatted "Invalid tier"
            return
        }

        val amount = args.getOrNull(2)?.toIntOrNull() ?: 1

        var type = XMaterial.matchXMaterial(getConfig().getGeneric("drop-item.material", "REDSTONE_TORCH_ON")).get().parseMaterial()!!
        if (type.name == "REDSTONE_TORCH_OFF" && ServerVersion.version < ServerVersion.V1_13) {
            type = Material.valueOf("REDSTONE_TORCH_ON")
        }

        val flare = item {
            this.type = type
            this.amount = amount

            name = getConfig().getGeneric("drop-item.name", "Envoy Flare")
            lore(
                    "&" + getConfig().getGeneric("drop-item.tier-color", "c") + tierName,
                    *getConfig().getList("drop-item.lore", mutableListOf()).toTypedArray()
            )
        }

        // apply NBT to flare item
        val nbt = NBTItem(flare, true)
        nbt.setBoolean(Constants.KEY_IS_FLARE, true)
        nbt.setString(Constants.KEY_TIER, tierName)

        player.inventory.addItem(flare)
    }
}