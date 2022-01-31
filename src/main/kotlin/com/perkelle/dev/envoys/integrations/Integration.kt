package com.perkelle.dev.envoys.integrations

import com.perkelle.dev.envoys.config.Config
import org.bukkit.Bukkit
import org.bukkit.Location

abstract class Integration : IntegrationSettings {

    fun isInstalled() = Bukkit.getServer().pluginManager.isPluginEnabled(pluginName)

    abstract fun isIntegrationEnabled(config: Config): Boolean

    abstract fun canPlaceEnvoy(loc: Location): Boolean

    abstract fun init()

    companion object {
        fun register(integration: Integration) {
            IntegrationProvider.activeIntegrations.add(integration)
            integration.init()
        }
    }
}