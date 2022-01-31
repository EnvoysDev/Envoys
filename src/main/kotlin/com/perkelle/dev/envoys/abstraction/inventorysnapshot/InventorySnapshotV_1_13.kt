package com.perkelle.dev.envoys.abstraction.inventorysnapshot

import org.bukkit.block.Chest

class InventorySnapshotV_1_13 : IInventorySnapshot {
    override fun getInventorySnapshot(chest: Chest) = chest.snapshotInventory
}
