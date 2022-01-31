package com.perkelle.dev.envoys.envoys.refill

import com.perkelle.dev.envoys.envoys.items.tiers.Tier
import org.bukkit.Location

class PredefinedEnvoy(val id: Int, tier: Tier, isRandomLocation: Boolean, location: Location, val chance: Int): Envoy(tier, isRandomLocation, location)