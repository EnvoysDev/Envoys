package com.perkelle.dev.envoys.particles

import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.utils.equalsIgnoreCase
import org.bukkit.Location
import xyz.xenondevs.particle.ParticleEffect

interface ParticleStyle {
    val effect: ParticleEffect?
        get() = ParticleEffect.values().firstOrNull { it.name equalsIgnoreCase getConfig().getGeneric("particles.particle-type", "REDSTONE") }

    val isColourPermitted: Boolean
            get() = effect == ParticleEffect.REDSTONE || effect == ParticleEffect.SPELL_MOB || effect == ParticleEffect.SPELL_MOB_AMBIENT

    fun spawnParticles(loc: Location)
}