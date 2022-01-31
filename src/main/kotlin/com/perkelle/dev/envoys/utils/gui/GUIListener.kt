package com.perkelle.dev.envoys.utils.gui

import com.perkelle.dev.envoys.inventories.editMainMenu
import com.perkelle.dev.envoys.utils.invTitle
import com.perkelle.dev.envoys.utils.translateColour
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class GUIListener: Listener {

    companion object {
        val guis = mutableListOf<DSLGuiBuilder>()
    }

    @EventHandler fun onClick(e: InventoryClickEvent) {
        val inv = e.clickedInventory ?: return

        val gui = guis.firstOrNull { (it.compareByName && it.title == inv.invTitle?.translateColour()) || (!it.compareByName && it.inv == inv) } ?: return
        if(!gui.listen) return

        if(gui.cancelClick) e.isCancelled = true
        gui.contents[e.slot]?.second?.invoke(e)
    }
}
