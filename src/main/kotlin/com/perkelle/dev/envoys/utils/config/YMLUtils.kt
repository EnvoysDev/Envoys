package com.perkelle.dev.envoys.utils.config

import org.bukkit.configuration.ConfigurationSection
import java.lang.Exception

class YMLUtils(val config: ConfigurationSection) {

    inline fun <reified T> getGeneric(key: String, default: T): T {
        try {
            val generic = config.get(key)!!
            if (generic::class.java == T::class.java) return generic as T
        } catch(_: Exception) {}
        return default
    }

    inline fun <reified T> getGenericOrNull(key: String): T? {
        try {
            val generic = config.get(key) !!
            if (generic::class.java == T::class.java) return generic as T
        } catch(_: Exception) {}
        return null
    }

    fun getConfigurationSection(key: String) = config.getConfigurationSection(key)

    fun getList(key: String, default: MutableList<String>): MutableList<String> {
        return if(config.contains(key)) config.getStringList(key)
                else default
    }

    fun getList(key: String) = getList(key, mutableListOf())

    fun getListOrNull(key: String): MutableList<String>? {
        return if(config.contains(key)) config.getStringList(key)
        else null
    }
}