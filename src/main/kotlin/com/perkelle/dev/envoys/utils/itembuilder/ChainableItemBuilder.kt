package com.perkelle.dev.envoys.utils.itembuilder

import com.perkelle.dev.envoys.abstraction.durability.IDurabilityHandler
import com.perkelle.dev.envoys.utils.translateColour
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta

class ChainableItemBuilder {

    // Constants
    val maxDurability: Short
        get() = type.maxDurability

    // Requires a value, therefore lateinit
    private lateinit var type : Material

    private var lore = listOf<String>()
    private val enchants = mutableMapOf<Enchantment, Int>()
    private var itemFlags = mutableMapOf<ItemFlag, Boolean>()
    private var name : String? = null
    private var amount = 1
    private var durability : Short = 0
    private var colour : DyeColor? = null // Glass or wool, not armour
    private var armourColor : Color? = null

    fun type(type: Material): ChainableItemBuilder {
        this.type = type
        return this
    }

    fun name(name : String) : ChainableItemBuilder {
        this.name = name
        return this
    }

    fun amount(amount : Int) : ChainableItemBuilder {
        this.amount = amount
        return this
    }

    fun durability(durability : Short) : ChainableItemBuilder {
        this.durability = durability
        return this
    }

    fun colour(colour : DyeColor) : ChainableItemBuilder {
        this.colour = colour
        return this
    }

    fun armourColour(armourColor : Color) : ChainableItemBuilder {
        this.armourColor = armourColor
        return this
    }

    fun lore(vararg lines : String) : ChainableItemBuilder {
        this.lore = lines.toList()
        return this
    }

    fun enchant(enchant : Enchantment, level : Int) : ChainableItemBuilder {
        enchants[enchant] = level
        return this
    }

    fun itemFlags(
            hideEnchants : Boolean = false,
            hideUnbreakable : Boolean = false,
            hideAttributes : Boolean = false,
            hideDestroys : Boolean = false,
            hidePotionEffects : Boolean = false,
            hidePlacedOn : Boolean = false
    ) : ChainableItemBuilder {
        itemFlags[ItemFlag.HIDE_ENCHANTS] = hideEnchants
        itemFlags[ItemFlag.HIDE_UNBREAKABLE] = hideUnbreakable
        itemFlags[ItemFlag.HIDE_ATTRIBUTES] = hideAttributes
        itemFlags[ItemFlag.HIDE_DESTROYS] = hideDestroys
        itemFlags[ItemFlag.HIDE_POTION_EFFECTS] = hidePotionEffects
        itemFlags[ItemFlag.HIDE_PLACED_ON] = hidePlacedOn

        return this
    }

    fun build() : ItemStack {
        var stack = ItemStack(type)
        var meta = stack.itemMeta // Make var so we can make it ArmorMeta or SkullMeta

        // Stack variables
        stack.amount = amount
        stack = IDurabilityHandler.instance.setDurability(stack, durability.toInt())

        // Default or nullable meta variables
        name?.let { meta?.setDisplayName(it.translateColour()) }
        colour?.let { stack.durability = it.ordinal.toShort() }
        armourColor?.let {
            val armourMeta = meta as LeatherArmorMeta
            armourMeta.setColor(colour?.color)
            meta = armourMeta
        }

        // Private variables
        meta?.lore = lore.map(String::translateColour)
        stack.addUnsafeEnchantments(enchants)
        meta?.addItemFlags(*itemFlags.filter { it.value }.map { it.key }.toTypedArray())

        stack.itemMeta = meta

        return stack
    }
}
