package com.perkelle.dev.envoys.inventories

import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.config.sendFormatted
import com.perkelle.dev.envoys.envoys.items.contents.ContentsManager
import com.perkelle.dev.envoys.listeners.ChatListener
import com.perkelle.dev.envoys.utils.XMaterial
import com.perkelle.dev.envoys.utils.gui.gui
import com.perkelle.dev.envoys.utils.itembuilder.item
import com.perkelle.dev.envoys.utils.runLater
import com.perkelle.dev.envoys.utils.saveItem
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.inventory.ItemFlag

private val contentsManager = ContentsManager()

fun getItemEditMenu() = gui(title = "&cEdit Items") {
    // Fill background with gray stained glass
    val stainedGlass = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem() ?: return@gui
    val stainedGlassMeta = stainedGlass.itemMeta!!
    stainedGlassMeta.setDisplayName("" + ChatColor.GREEN)
    stainedGlassMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
    stainedGlass.itemMeta = stainedGlassMeta

    fill(stainedGlass)

    // Add command
    addItem(11, item {
        type = XMaterial.COMMAND_BLOCK.parseMaterial()!!
        name = "&aAdd Command"
        lore("&7Click to add a command")
    }) {
        whoClicked.closeInventory()

        // Get command properties
        whoClicked sendFormatted "Type the item name:"
        ChatListener.callbacks[whoClicked as Player] = { nameEvent -> // Move to conversation API?
            nameEvent.isCancelled = true

            // Validate name
            val name = nameEvent.message
            if(name.contains(" "))
                whoClicked sendFormatted "Item names cannot contain spaces"

            else {
                // Get item chance
                whoClicked sendFormatted "Type the item chance:"
                ChatListener.callbacks[whoClicked as Player] = { chanceEvent ->
                    val msg = chanceEvent.message

                    chanceEvent.isCancelled = true

                    // Check that the chance is a number
                    if(msg.toDoubleOrNull() == null)
                        chanceEvent.player sendFormatted "The chance must be an integer or a decimal"
                    else {
                        // Check the chance is between 1 and 100
                        val chance = msg.toDouble()
                        if(chance > 100.0 || chance < 0.0) {
                            chanceEvent.player sendFormatted "The chance must be between 1 and 100"
                        }

                        // We may proceed, checks have passed
                        else {
                            // Get the commands
                            whoClicked sendFormatted "Type the command, without a slash:"
                            ChatListener.callbacks[whoClicked as Player] = { commandEvent ->
                                val command = commandEvent.message
                                commandEvent.isCancelled = true

                                // Update the config
                                getConfig().config.set("contents.items.$name.type", "command")
                                getConfig().config.set("contents.items.$name.chance", chance)
                                getConfig().config.set("contents.items.$name.commands", listOf(command))
                                getConfig().save()

                                contentsManager.clearContents()
                                contentsManager.loadContents()

                                whoClicked sendFormatted "Added $name as /$command"
                            }
                        }
                    }
                }
            }
        }
    }

    addItem(13, item {
        type = Material.EMERALD_BLOCK
        name = "&aAdd Item"
        lore("&7Drag and drop an item from your inventory onto this item to add it")
    }) {
        if(action == InventoryAction.SWAP_WITH_CURSOR) {
            val item = cursor?.clone() ?: return@addItem

            runLater(2L) {
                whoClicked sendFormatted "Type the item's config name:"

                ChatListener.callbacks[whoClicked as Player] = { nameEvent ->
                    val name = nameEvent.message

                    nameEvent.isCancelled = true

                    if (name.contains(" "))
                        nameEvent.player sendFormatted "The item name must not contain any spaces"
                    else {
                        whoClicked sendFormatted "Type the item's chance:"

                        ChatListener.callbacks[whoClicked as Player] = { e ->
                            val msg = e.message
                            e.isCancelled = true

                            if (msg.toDoubleOrNull() == null)
                                e.player sendFormatted "The item chance must be an integer or a decimal"
                            else {
                                val chance = msg.toDouble()
                                if (chance > 100.0 || chance < 0.0) {
                                    e.player sendFormatted "The item chance must be between 1 and 100"
                                } else {
                                    item.saveItem(name, chance)
                                    getConfig().save()

                                    contentsManager.clearContents()
                                    contentsManager.loadContents()

                                    whoClicked sendFormatted "Added $name!"
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    addItem(15, item {
        type = Material.BARRIER
        name = "&aDelete Items"
        lore("&7Click to select items to delete from the item list")
    }) {
        whoClicked.openInventory(getItemDeleteMenu())
    }
}