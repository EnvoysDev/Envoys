package com.perkelle.dev.envoys.particles

import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.utils.equalsIgnoreCase
import org.bukkit.Bukkit
import org.bukkit.Color
import xyz.xenondevs.particle.data.color.RegularColor

fun getParticleStyle() = ParticleStyles.values().firstOrNull { it.configName equalsIgnoreCase getConfig().getGeneric("particles.style", "circle") }?.instance

fun Color.toRegularColor() = RegularColor(red, green, blue)
