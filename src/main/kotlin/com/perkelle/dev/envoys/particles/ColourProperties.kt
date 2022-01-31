package com.perkelle.dev.envoys.particles

import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.utils.Colours
import com.perkelle.dev.envoys.utils.equalsIgnoreCase
import org.bukkit.Color

object ColourProperties {
    val IS_ENABLED: Boolean
        get() = getConfig().getGeneric("particles.colour.enabled", true)

    val COLOURS: List<Color>
        get() = getConfig().getList("particles.colour.colours", mutableListOf("RED")).mapNotNull { str -> Colours.values().firstOrNull { it.name equalsIgnoreCase str } }.map { it.color }

    fun selectColour(): Color {
        if(COLOURS.isEmpty()) return Color.RED // Default
        return COLOURS.random()
    }
}