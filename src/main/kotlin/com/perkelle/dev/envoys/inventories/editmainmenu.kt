package com.perkelle.dev.envoys.inventories

import com.perkelle.dev.envoys.utils.XMaterial
import com.perkelle.dev.envoys.utils.gui.gui
import com.perkelle.dev.envoys.utils.itembuilder.item
import org.bukkit.ChatColor
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag

val editMainMenu = gui(3, "&cEnvoy Edit Menu") {
    cancelClick = true
    listen = true

    val stainedGlass = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem() ?: return@gui
    val stainedGlassMeta = stainedGlass.itemMeta!!
    stainedGlassMeta.setDisplayName("" + ChatColor.GREEN)
    stainedGlassMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
    stainedGlass.itemMeta = stainedGlassMeta

    fill(stainedGlass)

    addItem(12, item {
        type = Material.CHEST
        name = "&aTiers"
        lore("&7Click to edit tiers")
    }) {
        whoClicked.openInventory(getTierMenu())
    }

    addItem(14, item {
        type = Material.IRON_SWORD
        name = "&aItems"
        lore("&7Click to edit items")
    }) {
        whoClicked.openInventory(getItemEditMenu())
    }
}