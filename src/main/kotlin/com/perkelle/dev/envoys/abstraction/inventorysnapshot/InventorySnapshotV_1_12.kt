package com.perkelle.dev.envoys.abstraction.inventorysnapshot

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.inventory.Inventory

class InventorySnapshotV_1_12 : IInventorySnapshot {
    override fun getInventorySnapshot(chest: Chest): Inventory {
        val inv = Bukkit.createInventory(null, chest.inventory.size)

        val contents = chest.inventory.contents
                ?.filterNotNull()!!
                .filterNot { it.type == Material.AIR }

        for ((i, item) in contents.withIndex()) {
            inv.setItem(i, item.clone())
        }

        return inv
    }
}
