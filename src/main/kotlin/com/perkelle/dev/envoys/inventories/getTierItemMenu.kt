package com.perkelle.dev.envoys.inventories

import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.config.sendFormatted
import com.perkelle.dev.envoys.envoys.items.contents.ContentsManager
import com.perkelle.dev.envoys.envoys.items.contents.EnvoyCommand
import com.perkelle.dev.envoys.envoys.items.contents.EnvoyItem
import com.perkelle.dev.envoys.envoys.items.tiers.Tier
import com.perkelle.dev.envoys.envoys.items.tiers.TierManager
import com.perkelle.dev.envoys.listeners.ChatListener
import com.perkelle.dev.envoys.utils.*
import com.perkelle.dev.envoys.utils.gui.gui
import com.perkelle.dev.envoys.utils.itembuilder.item
import org.bukkit.Material
import org.bukkit.entity.Player

private val contentsManager = ContentsManager()
private val tierManager = TierManager()

fun getTierMenu(tier: Tier) = gui(6, "&cEdit ${tier.name}") {
    val items = tier.contents

    addItem(0, item {
        type = Material.EMERALD_BLOCK
        name = "&aAdd Item"
        lore("&7Click to add an item to &6${tier.name}")
    }) {
        whoClicked.closeInventory()

        whoClicked sendFormatted "Type the item name"
        ChatListener.callbacks[whoClicked as Player] = { e ->
            val name = e.message
            val item = contentsManager.getContentByName(name)

            e.isCancelled = true

            if(item == null)
                whoClicked sendFormatted "Invalid item. The item must be added in the config, or via /envoys edit"
            else {
                getConfig().config.set("contents.tiers.${tier.name}.items", getConfig().getList("contents.tiers.${tier.name}.items").with(name))
                getConfig().save()

                tierManager.clearTiers()
                tierManager.loadTiers()

                whoClicked sendFormatted "Added $name to ${tier.name}"
            }
        }
    }

    val max =
            if(items.size >= 45) 45
            else items.size

    for(i in 0 until max) {
        val content = items[i]

        addItem(i + 9, item {
            type =
                    if(content is EnvoyItem) (content.getAs<EnvoyItem>()).material
                    else XMaterial.COMMAND_BLOCK.parseMaterial()!!

            name = "&a${content.name}"

            if(content is EnvoyItem) {
                lore(
                        "&cClick to delete",
                        "&6Name: &a${content.displayName?.translateColour()}",
                        "&6Amount: &a${content.amount}",
                        "&6Lore: &a${content.lore.joinToString("\n", transform = String::translateColour)}"
                )
            } else if(content is EnvoyCommand) {
                lore("&cClick to delete", *content.commands.map { "&a$it".translateColour() }.toTypedArray())
            }
        }) {
            getConfig().config.set("contents.tiers.${tier.name}.items", getConfig().getList("contents.tiers.${tier.name}.items").without(content.name))
            getConfig().save()

            tierManager.clearTiers()
            tierManager.loadTiers()

            whoClicked sendFormatted "Removed ${content.name} from ${tier.name}"
        }
    }
}