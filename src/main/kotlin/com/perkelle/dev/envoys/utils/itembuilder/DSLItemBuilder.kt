package com.perkelle.dev.envoys.utils.itembuilder

import com.perkelle.dev.envoys.utils.translateColour
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta

class DSLItemBuilder {

    // Constants (?)
    val maxDurability: Short
        get() = type.maxDurability

    // Requires a value, therefore lateinit
    lateinit var type : Material

    // Private variables are set by methods, e.g. DSLItemBuilder#lore()
    private var lore = listOf<String>()
    private val enchants = mutableMapOf<Enchantment, Int>()
    private var itemFlags = mutableMapOf<ItemFlag, Boolean>()

    // Nullable variables, or variables with default values, can be accessed directly
    var name : String? = null
    var amount = 1
    var durability : Short = 0
    var colour : DyeColor? = null // Glass or wool, not armour
    var armourColor : Color? = null

    fun lore(vararg lines : String) {
        lore = lines.toList()
    }

    infix fun Enchantment.apply(level : Int) {
        enchants[this] = level
    }

    fun itemFlags(block : ItemFlags.() -> Unit) {
        val flags = ItemFlags().also(block)

        itemFlags[ItemFlag.HIDE_ENCHANTS] = flags.hideEnchants
        itemFlags[ItemFlag.HIDE_UNBREAKABLE] = flags.hideUnbreakable
        itemFlags[ItemFlag.HIDE_ATTRIBUTES] = flags.hideAttributes
        itemFlags[ItemFlag.HIDE_DESTROYS] = flags.hideDestroys
        itemFlags[ItemFlag.HIDE_POTION_EFFECTS] = flags.hidePotionEffects
        itemFlags[ItemFlag.HIDE_PLACED_ON] = flags.hidePlacedOn
    }

    fun getStack() : ItemStack {
        val stack = ItemStack(type)
        var meta = stack.itemMeta // Make var so we can make it ArmorMeta or SkullMeta

        // Stack variables
        stack.amount = amount
        stack.durability = durability

        // Default or nullable meta variables
        name?.let { meta?.setDisplayName(it.translateColour()) }
        colour?.let { stack.durability = it.ordinal.toShort() }
        armourColor?.let {
            val armourMeta = meta as LeatherArmorMeta
            armourMeta.setColor(armourColor)
            meta = armourMeta
        }

        // Private variables
        meta?.lore = lore.map(String::translateColour)
        stack.addUnsafeEnchantments(enchants)
        meta?.addItemFlags(*itemFlags.filter { it.value }.map { it.key }.toTypedArray())

        stack.itemMeta = meta

        return stack
    }

    class ItemFlags {
        var hideEnchants = false
        var hideUnbreakable = false
        var hideAttributes = false
        var hideDestroys = false
        var hidePotionEffects = false
        var hidePlacedOn = false
    }
}
