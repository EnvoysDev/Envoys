package com.perkelle.dev.envoys.abstraction.custommodeldata

import org.bukkit.inventory.meta.ItemMeta

class CustomModelDataAvailable : CustomModelDataHandler {
    override fun getData(itemMeta: ItemMeta): Int? {
        if(!hasData(itemMeta)) {
            return null
        }

        return itemMeta.customModelData
    }

    override fun hasData(itemMeta: ItemMeta): Boolean {
        return itemMeta.hasCustomModelData()
    }

    override fun setData(itemMeta: ItemMeta, data: Int): ItemMeta {
        itemMeta.setCustomModelData(data)
        return itemMeta
    }
}