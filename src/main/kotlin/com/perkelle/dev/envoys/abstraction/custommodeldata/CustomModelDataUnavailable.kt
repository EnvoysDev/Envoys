package com.perkelle.dev.envoys.abstraction.custommodeldata

import org.bukkit.inventory.meta.ItemMeta

class CustomModelDataUnavailable : CustomModelDataHandler {
    override fun getData(itemMeta: ItemMeta) = null
    override fun hasData(itemMeta: ItemMeta) = false
    override fun setData(itemMeta: ItemMeta, data: Int) = itemMeta
}