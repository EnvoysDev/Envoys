package com.perkelle.dev.envoys.envoys.items.contents

import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.envoys.items.envoyItem
import com.perkelle.dev.envoys.utils.XMaterial
import com.perkelle.dev.envoys.utils.config.YMLUtils
import com.perkelle.dev.envoys.utils.isInt
import com.perkelle.dev.envoys.utils.nbt.NBTObject
import com.perkelle.dev.envoys.utils.nbt.NBTType
import com.perkelle.dev.envoys.utils.potionTypes
import org.bukkit.Bukkit
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class ContentsManager {

    companion object {
        private val contents = mutableListOf<EnvoyContent>()
    }

    fun loadContents() {
        val itemSection = getConfig().getConfigurationSection("contents.items")
        val itemNames = itemSection?.getKeys(false) ?: listOf<String>()

        for(itemName in itemNames) {
            val item = YMLUtils(itemSection?.getConfigurationSection(itemName) ?: continue)

            val type = ContentType.values().firstOrNull { it.configName.equals(item.getGenericOrNull("type"), true) }
            if(type == null) {
                Bukkit.getLogger().warning("Invalid contents type")
                continue
            }

            val chance = item.getGenericOrNull<Double>("chance") ?: item.getGenericOrNull<Int>("chance")?.toDouble()
            if(chance == null) {
                Bukkit.getLogger().warning("Invalid contents chance")
                continue
            }

            if(type == ContentType.ITEM) {
                try {
                    val envoyItem = envoyItem(chance, itemName) {
                        val idStr = item.config.get("id")?.toString()
                        if (idStr == null) {
                            Bukkit.getLogger().warning("Invalid item ID")
                            throw Exception("Invalid item ID")
                        }

                        durability = item.getGeneric("data", 0)

                        // TODO: Make this readable
                        material = if(idStr.isInt()) {
                            XMaterial.matchXMaterial(idStr.toInt(), durability.toByte()).orElse(null)?.parseMaterial() ?: throw Exception("No material found")
                        } else {
                            if(durability == 0) {
                                XMaterial.matchXMaterial(idStr).orElse(null)?.parseMaterial() ?: throw Exception("No material found")
                            } else {
                                XMaterial.matchXMaterial(idStr, durability.toByte()).orElseThrow { java.lang.Exception("No material found") }.parseMaterial()!!
                            }
                        }

                        displayName = item.getGenericOrNull("name")
                        amount = item.getGeneric("amount", 1)
                        lore = item.getList("lore", mutableListOf())

                        val damage = item.getGenericOrNull<Int>("damage")
                        if (damage != null && damage != 0) {
                            durability = damage
                        }

                        val enchantsRaw = item.getList("enchants", mutableListOf()).map { it.split(",") }

                        for ((enchantName, enchantLevel) in enchantsRaw) {
                            val enchant = Enchantment.values().firstOrNull { it.name.equals(enchantName, true) }
                            val level = enchantLevel.toIntOrNull()

                            if (enchant == null) {
                                Bukkit.getLogger().warning("Invalid item enchant: $enchantName")
                                continue
                            }
                            if (level == null) {
                                Bukkit.getLogger().warning("Invalid item level: $enchantLevel")
                                continue
                            }

                            enchants[enchant] = level
                        }

                        nbt = item.config.getMapList("nbt").mapNotNull { section ->
                            val nbtType = NBTType.values().firstOrNull { it.configName == section["type"] }
                            if(nbtType == null) {
                                Bukkit.getLogger().warning("Invalid NBT datatype: ${section["type"]}. Valid data types: [${NBTType.values().joinToString(", ")}]")
                                return@mapNotNull null
                            }

                            val key = section["key"]?.toString()
                            val value = section["value"]
                            if(key == null || value == null) {
                                Bukkit.getLogger().warning("Missing NBT key or value for $itemName")
                                return@mapNotNull null
                            }

                            NBTObject(nbtType, key, value)
                        }

                        // Apply item flags
                        flags = item.config.getStringList("flags").mapNotNull(ItemFlag::valueOf)

                        // Apply potion effects
                        if(potionTypes.contains(XMaterial.matchXMaterial(material)) && item.config.contains("effect")) {
                            val effectSettings = YMLUtils(item.getConfigurationSection("effect")!!)
                            potionEffect = getEffectType(effectSettings)
                        }

                        // Get custom model data
                        customModelData = item.getGeneric("custommodeldata", 0)
                    }

                    contents.add(envoyItem)
                } catch(ex: Exception) {
                    Bukkit.getLogger().warning("[Envoys] Failed to register $itemName")
                    ex.printStackTrace()
                }
            } else if(type == ContentType.COMMAND) {
                val commands = item.getListOrNull("commands")
                if(commands == null) {
                    Bukkit.getLogger().warning("Invalid list of commands")
                    continue
                }

                contents.add(EnvoyCommand(chance, itemName, commands))
            }
        }
    }

    fun getContentByName(name: String) = contents.firstOrNull { it.name == name }

    fun clearContents() = contents.clear()

    fun getAllContents() = contents

    private fun getEffectType(config: YMLUtils): PotionEffect? {
        val name = config.getGenericOrNull<String>("type") ?: return null
        val effect = PotionEffectType.values().firstOrNull { name.equals(it.name, true) } ?: return null

        val level = config.getGenericOrNull<Int>("level") ?: return null
        val duration = config.getGenericOrNull<Int>("duration") ?: return null

        val ambient = config.getGeneric("ambient", true)
        val particles = config.getGeneric("particles", true)

        return PotionEffect(effect, duration * 20, level - 1, ambient, particles)
    }
}