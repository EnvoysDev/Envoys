package com.perkelle.dev.envoys.abstraction.durability

import com.perkelle.dev.envoys.abstraction.ServerVersion
import org.bukkit.inventory.ItemStack

interface IDurabilityHandler {
    fun getDurability(itemStack: ItemStack): Int
    fun setDurability(itemStack: ItemStack, durability: Int): ItemStack

    companion object {
        val instance: IDurabilityHandler by lazy {
            if(ServerVersion.version >= ServerVersion.V1_13) {
                DurabilityHandlerV_1_13()
            } else {
                DurabilityHandlerV_1_12()
            }
        }
    }
}