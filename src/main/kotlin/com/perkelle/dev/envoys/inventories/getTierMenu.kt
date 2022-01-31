package com.perkelle.dev.envoys.inventories

import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.config.sendFormatted
import com.perkelle.dev.envoys.envoys.items.contents.EnvoyContent
import com.perkelle.dev.envoys.envoys.items.tiers.TierManager
import com.perkelle.dev.envoys.listeners.ChatListener
import com.perkelle.dev.envoys.utils.gui.gui
import com.perkelle.dev.envoys.utils.itembuilder.item
import com.perkelle.dev.envoys.utils.translateColour
import org.bukkit.Material
import org.bukkit.entity.Player

private val tierManager = TierManager()

fun getTierMenu() = gui(6, "&cTier Selection") {
    val tiers = tierManager.getTiers()
    val max =
            if(tiers.size >= 54) 54
            else tiers.size

    for(i in 0 until max) {
        val tier = tiers[i]

        addItem(i, item {
            type = Material.CHEST
            name = tier.name.translateColour()
            lore("&6Left &aclick to edit items", "&6Right &aclick to edit tier name & chance")
        }) {
            if(isLeftClick) {
                whoClicked.openInventory(getTierMenu(tier))
            } else if(isRightClick) {
                whoClicked.closeInventory()

                whoClicked sendFormatted "Type the tier's updated name:"
                ChatListener.callbacks[whoClicked as Player] = { nameEvent ->
                    val name = nameEvent.message

                    nameEvent.isCancelled = true

                    if(name.contains(" "))
                        nameEvent.player sendFormatted "The tier name must not contain any spaces"
                    else {
                        whoClicked sendFormatted "Type the tier's updated chance:"

                        ChatListener.callbacks[whoClicked as Player] = { e ->
                            val msg = e.message

                            e.isCancelled = true

                            if(msg.toDoubleOrNull() == null)
                                e.player sendFormatted "The tier chance must be an integer or a decimal"
                            else {
                                val chance = msg.toDouble()
                                if(chance > 100.0 || chance < 0.0) {
                                    e.player sendFormatted "The tier chance must be between 1 and 100"
                                } else {
                                    val items = tier.contents.map(EnvoyContent::name)

                                    getConfig().config.set("contents.tiers.${tier.name}", null)

                                    getConfig().config.set("contents.tiers.$name.chance", chance)
                                    getConfig().config.set("contents.tiers.$name.items", items)

                                    getConfig().save()
                                    tierManager.clearTiers()
                                    tierManager.loadTiers()

                                    whoClicked sendFormatted "Updated!"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}