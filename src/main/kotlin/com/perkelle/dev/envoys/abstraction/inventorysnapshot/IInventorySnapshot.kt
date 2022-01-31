package com.perkelle.dev.envoys.abstraction.inventorysnapshot

import com.perkelle.dev.envoys.abstraction.ServerVersion
import org.bukkit.block.Chest
import org.bukkit.inventory.Inventory

interface IInventorySnapshot {
    fun getInventorySnapshot(chest: Chest): Inventory

    companion object {
        val instance: IInventorySnapshot by lazy {
            if(ServerVersion.version >= ServerVersion.V1_13) {
                InventorySnapshotV_1_13()
            } else {
                InventorySnapshotV_1_12()
            }
        }
    }
}