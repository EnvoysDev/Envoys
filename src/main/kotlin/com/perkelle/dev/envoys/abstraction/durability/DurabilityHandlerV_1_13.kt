package com.perkelle.dev.envoys.abstraction.durability

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

class DurabilityHandlerV_1_13 : IDurabilityHandler {

    override fun getDurability(itemStack: ItemStack) = (itemStack.itemMeta as? Damageable)?.damage ?: 0

    override fun setDurability(itemStack: ItemStack, durability: Int): ItemStack {
        val meta = itemStack.itemMeta
        if(meta is Damageable) {
            meta.damage = durability
        }
        itemStack.itemMeta = meta
        return itemStack
    }
}