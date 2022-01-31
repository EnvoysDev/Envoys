package com.perkelle.dev.envoys.utils.gui

import com.perkelle.dev.envoys.utils.translateColour
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class DSLGuiBuilder(val rows: Int = 3, val title: String? = null) {

    val inv =
            if(title == null) Bukkit.createInventory(null, rows * 9)
            else Bukkit.createInventory(null, rows * 9, title.translateColour())

    var cancelClick = true
    var listen = true
    var compareByName = false

    internal val contents = mutableMapOf<Int, Pair<ItemStack, InventoryClickEvent.() -> Unit>>()

    init {
        GUIListener.guis.add(this)
    }

    fun addItem(slot: Int, itemStack : ItemStack, clickEvent: InventoryClickEvent.() -> Unit = {}) {
        contents[slot] = itemStack to clickEvent
        inv.setItem(slot, itemStack)
    }

    fun fill(itemStack : ItemStack, clickEvent: InventoryClickEvent.() -> Unit = {}) {
        for(i in 0 until rows * 9) {
            if(contents[i] == null) {
                contents[i] = itemStack to clickEvent
                inv.setItem(i, itemStack)
            }
        }
    }

    fun build() : Inventory {
        return inv
    }
}
