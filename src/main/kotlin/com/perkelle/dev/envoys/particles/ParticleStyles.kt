package com.perkelle.dev.envoys.particles

import com.perkelle.dev.envoys.particles.impl.CircleStyle

enum class ParticleStyles(val configName: String, val instance: ParticleStyle) {
    CIRLCE("circle", CircleStyle()),
}