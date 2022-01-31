package com.perkelle.dev.envoys.abstraction.durability

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

class DurabilityHandlerV_1_12 : IDurabilityHandler {

    override fun getDurability(itemStack: ItemStack) = itemStack.durability.toInt()

    override fun setDurability(itemStack: ItemStack, durability: Int): ItemStack {
        itemStack.durability = durability.toShort()
        return itemStack
    }
}