package com.perkelle.dev.envoys.integrations.impl

import com.perkelle.dev.envoys.config.Config
import com.perkelle.dev.envoys.integrations.Integration
import me.ryanhamshire.GriefPrevention.GriefPrevention
import org.bukkit.Location

class GriefPreventionIntegration : Integration() {

    override val pluginName = "GriefPrevention"

    private val gp: GriefPrevention by lazy {
        GriefPrevention.instance
    }

    override fun isIntegrationEnabled(config: Config) = config.getGeneric("random-location.integrations.griefprevention.enabled", false)

    override fun init() {}

    override fun canPlaceEnvoy(loc: Location): Boolean {
        if(!gp.claimsEnabledForWorld(loc.world)) return true
        val claim = gp.dataStore.getClaimAt(loc, false, null)
        return claim == null
    }
}