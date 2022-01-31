package com.perkelle.dev.envoys.envoys.items.contents

import com.perkelle.dev.envoys.abstraction.custommodeldata.CustomModelDataHandler
import com.perkelle.dev.envoys.abstraction.durability.IDurabilityHandler
import com.perkelle.dev.envoys.utils.nbt.NBTAPI
import com.perkelle.dev.envoys.utils.nbt.NBTObject
import com.perkelle.dev.envoys.utils.translateColour
import de.tr7zw.nbtapi.NBTItem
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect

open class EnvoyItem(
        chance: Double,
        name: String
) : EnvoyContent(ContentType.ITEM, name, chance) {

    private val nbtApi = NBTAPI()

    lateinit var material: Material
    var durability: Int = 0
    var displayName: String? = null
    var amount: Int = 1
    var lore = emptyList<String>()
    val enchants = mutableMapOf<Enchantment, Int>()
    var nbt = emptyList<NBTObject>()
    var flags = emptyList<ItemFlag>()
    var potionEffect: PotionEffect? = null
    var customModelData = 0

    fun getItemStack(): ItemStack {
        // Create the itemstack
        var stack = ItemStack(material)

        // Apply NBT
        // This method overwrites other properties such as the name etc so call it first
        nbtApi.loadFromFile(name, stack)

        var meta = stack.itemMeta!!

        // Apply amount, item name and lore
        stack.amount = amount

        displayName?.apply { meta.setDisplayName(this.translateColour()) }
        if (lore.isNotEmpty()) meta.lore = lore.map(String::translateColour)

        // Handle enchants
        if (material == Material.ENCHANTED_BOOK) {
            // Different method to handle books
            val enchantMeta = meta as? EnchantmentStorageMeta
            enchants.forEach { (enchant, level) ->
                enchantMeta?.addStoredEnchant(enchant, level, true)
            }
        } else {
            enchants.forEach { (enchant, level) ->
                meta.addEnchant(enchant, level, true)
            }
        }

        // Apply durability
        stack = IDurabilityHandler.instance.setDurability(stack, durability)

        // Apply potion effects
        potionEffect?.apply {
            val potionMeta = meta as PotionMeta
            potionMeta.addCustomEffect(this, true)
        }

        // Copy over item flags
        meta.addItemFlags(*flags.toTypedArray())

        // Apply custom model data
        if (customModelData != 0) {
            meta = CustomModelDataHandler.instance.setData(meta, customModelData)
        }

        // Apply configured item meta
        stack.itemMeta = meta

        // Apply NBT pt2
        // Handle NBT in config.yml
        if (nbt.isNotEmpty()) {
            stack = nbtApi.applyToItem(stack, nbt)
        }

        return stack
    }
}