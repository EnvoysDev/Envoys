package com.perkelle.dev.envoys.abstraction.custommodeldata

import com.perkelle.dev.envoys.abstraction.ServerVersion
import org.bukkit.inventory.meta.ItemMeta

interface CustomModelDataHandler {
    fun getData(itemMeta: ItemMeta): Int?
    fun hasData(itemMeta: ItemMeta): Boolean
    fun setData(itemMeta: ItemMeta, data: Int): ItemMeta

    companion object {
        val instance: CustomModelDataHandler by lazy {
            if(ServerVersion.version >= ServerVersion.V1_14) {
                CustomModelDataAvailable()
            } else {
                CustomModelDataUnavailable()
            }
        }
    }
}