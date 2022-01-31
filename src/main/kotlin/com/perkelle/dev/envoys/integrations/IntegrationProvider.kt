package com.perkelle.dev.envoys.integrations

import com.perkelle.dev.envoys.config.Config
import com.perkelle.dev.envoys.integrations.impl.GriefPreventionIntegration
import org.bukkit.Location

object IntegrationProvider {

    val activeIntegrations = mutableListOf<Integration>()

    fun loadIntegrations(config: Config) {
        listOf(
                GriefPreventionIntegration()
        ).filter(Integration::isInstalled).filter { it.isIntegrationEnabled(config) }.forEach { Integration.register(it) }
    }

    fun canPlace(loc: Location) = activeIntegrations.all { it.canPlaceEnvoy(loc) }
}