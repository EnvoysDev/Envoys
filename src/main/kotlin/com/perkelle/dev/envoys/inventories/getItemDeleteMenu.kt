package com.perkelle.dev.envoys.inventories

import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.config.sendFormatted
import com.perkelle.dev.envoys.envoys.items.contents.ContentsManager
import com.perkelle.dev.envoys.envoys.items.contents.EnvoyCommand
import com.perkelle.dev.envoys.envoys.items.contents.EnvoyItem
import com.perkelle.dev.envoys.envoys.items.tiers.TierManager
import com.perkelle.dev.envoys.utils.*
import com.perkelle.dev.envoys.utils.gui.gui
import com.perkelle.dev.envoys.utils.itembuilder.item

private val contentsManager = ContentsManager()
private val tierManager = TierManager()

fun getItemDeleteMenu() = gui(6, "&cDelete Items") {
    val items = contentsManager.getAllContents()

    val max =
            if(items.size >= 45) 45
            else items.size

    for(i in 0 until max) {
        val content = items[i]
        val isItem = content is EnvoyItem
        val isCommand = content is EnvoyCommand

        addItem(i, item {
            type =
                    if(isItem) content.getAs<EnvoyItem>().material
                    else XMaterial.COMMAND_BLOCK.parseMaterial()!!

            name = "&a${content.name}"

            if(isItem) {
                content as EnvoyItem

                val extraLore =
                        if(content.lore.size > 1) content.lore.subList(1, content.lore.size).map(String::translateColour)
                        else listOf()

                lore(
                        "&cClick to delete",
                        "&6Name: &a${content.displayName?.translateColour()}",
                        "&6Amount: &a${content.amount}",
                        "&6Lore: &a${(content.lore.firstOrNull() ?: "").translateColour()}",
                        *extraLore.toTypedArray()
                )
            } else if(isCommand) {
                content as EnvoyCommand

                lore("&cClick to delete", *content.commands.map { "&a$it".translateColour() }.toTypedArray())
            }
        }) {
            getConfig().config.set("contents.items.${content.name}", null)
            getConfig().save()

            contentsManager.clearContents()
            contentsManager.loadContents()

            whoClicked.closeInventory()
            whoClicked sendFormatted "Removed ${content.name}"
        }
    }
}