package com.perkelle.dev.envoys.utils

import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

val Inventory.invTitle: String?
    get() = viewers
            .map { it.openInventory }
            .filter { it.type != InventoryType.CRAFTING }
            .filterNot { it.title.isBlank() }
            .firstOrNull()
            ?.title

