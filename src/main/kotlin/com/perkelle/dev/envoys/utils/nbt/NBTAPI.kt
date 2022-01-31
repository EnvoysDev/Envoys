package com.perkelle.dev.envoys.utils.nbt

import com.perkelle.dev.envoys.Envoys
import de.tr7zw.nbtapi.NBTContainer
import de.tr7zw.nbtapi.NBTItem
import org.bukkit.inventory.ItemStack
import java.io.File

class NBTAPI {

    fun applyToItem(itemStack: ItemStack, nbtData: List<NBTObject>): ItemStack {
        val item = NBTItem(itemStack)

        nbtData.forEach { data ->
            require(data.type.supported) { "Unsupported NBT data type: ${data.type.configName}" } // Throw exception if predicate returns false

            when(data.type) {
                NBTType.BYTE -> item.setByte(data.key, data.convertValue())
                NBTType.SHORT -> item.setShort(data.key, data.convertValue())
                NBTType.INT -> item.setInteger(data.key, data.convertValue())
                NBTType.LONG -> item.setLong(data.key, data.convertValue())
                NBTType.FLOAT -> item.setFloat(data.key, data.convertValue())
                NBTType.DOUBLE -> item.setDouble(data.key, data.convertValue())
                NBTType.STRING -> item.setString(data.key, data.convertValue())
                else -> {} // Shouldn't be able to get here
            }
        }

        return item.item
    }

    fun saveToFile(id: String, itemStack: ItemStack): Boolean {
        val nbt = NBTItem(itemStack)
        if(!nbt.hasNBTData()) return false

        val json = nbt.asNBTString()

        // Ensure NBT folder exists
        val folder = File(Envoys.instance.pl.dataFolder, "nbt")
        if(!folder.exists()) {
            folder.mkdir()
        }

        val nbtFile = File(folder, "$id.json")
        if(!nbtFile.exists()) {
            nbtFile.createNewFile()
            nbtFile.writeText(json)
        }

        return true
    }

    fun loadFromFile(id: String, itemStack: ItemStack) {
        // Ensure NBT folder exists
        val folder = File(Envoys.instance.pl.dataFolder, "nbt")
        if(!folder.exists()) {
            folder.mkdir()
        }

        val nbtFile = File(folder, "$id.json")
        if(nbtFile.exists()) {
            // Load JSON and parse
            val json = nbtFile.readText()
            val container = NBTContainer(json)

            // Apply NBT to item
            val nbtItem = NBTItem(itemStack, true)
            nbtItem.mergeCompound(container)
        }
    }
}