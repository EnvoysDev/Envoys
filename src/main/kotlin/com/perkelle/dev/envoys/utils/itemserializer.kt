package com.perkelle.dev.envoys.utils

import com.perkelle.dev.envoys.abstraction.custommodeldata.CustomModelDataHandler
import com.perkelle.dev.envoys.abstraction.durability.IDurabilityHandler
import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.utils.nbt.NBTAPI
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta

private val nbtApi: NBTAPI by lazy { // Only create an instance if NBTAPI is installed or we'll get errors
    NBTAPI()
}

fun ItemStack.saveItem(name: String, chance: Double) {
    val xMaterial = XMaterial.matchXMaterial(type)

    getConfig().config.set("contents.items.$name.type", "item")
    getConfig().config.set("contents.items.$name.chance", chance)
    getConfig().config.set("contents.items.$name.id", xMaterial.name)
    getConfig().config.set("contents.items.$name.data", xMaterial.data.toInt())
    getConfig().config.set("contents.items.$name.amount", amount)
    getConfig().config.set("contents.items.$name.enchants", enchantments.map { "${it.key.name},${it.value}" })

    if (hasItemMeta()) {
        if (itemMeta.hasDisplayName()) {
            getConfig().config.set("contents.items.$name.name", itemMeta.displayName.replace("§", "&"))
        }

        if (itemMeta.hasLore()) {
            getConfig().config.set("contents.items.$name.lore", itemMeta.lore?.map { it.replace("§", "&") })
        }

        if (itemMeta.itemFlags.isNotEmpty()) {
            getConfig().config.set("contents.items.$name.flags", itemMeta.itemFlags.map(ItemFlag::name))
        }

        if (CustomModelDataHandler.instance.hasData(itemMeta)) {
            getConfig().config.set("contents.items.$name.custommodeldata", CustomModelDataHandler.instance.getData(itemMeta))
        }

        val damage = IDurabilityHandler.instance.getDurability(this)
        if (damage != 0) {
            getConfig().config.set("contents.items.$name.damage", damage)
        }
    }

    // Encode potion
    if (potionTypes.contains(XMaterial.matchXMaterial(type))) {
        val potionMeta = itemMeta as PotionMeta
        potionMeta.customEffects.firstOrNull()?.let { effect ->
            getConfig().config.set("contents.items.$name.effect.type", effect.type.name)
            getConfig().config.set("contents.items.$name.effect.duration", effect.duration / 20)
            getConfig().config.set("contents.items.$name.effect.level", effect.amplifier + 1)
            getConfig().config.set("contents.items.$name.effect.ambient", effect.isAmbient)
            getConfig().config.set("contents.items.$name.effect.particles", effect.hasParticles())
        }
    }

    // Save NBT
    nbtApi.saveToFile(name, this)
}