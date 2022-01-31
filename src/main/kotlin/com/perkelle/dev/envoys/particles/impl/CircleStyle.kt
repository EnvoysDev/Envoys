package com.perkelle.dev.envoys.particles.impl

import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.particles.ColourProperties
import com.perkelle.dev.envoys.particles.ParticleStyle
import com.perkelle.dev.envoys.particles.toRegularColor
import org.bukkit.Bukkit
import org.bukkit.Location
import xyz.xenondevs.particle.data.color.ParticleColor

class CircleStyle : ParticleStyle {

    private val count = getConfig().getGeneric("particles.styles.circle.particle-count", 10)
    private val radius = getConfig().getGeneric("particles.styles.circle.radius", 2.0)

    override fun spawnParticles(loc: Location) {
        val points = loc.getCircle(radius, count)

        points.forEach { point ->
            if(ColourProperties.IS_ENABLED && isColourPermitted) {
                effect?.display(point, ColourProperties.selectColour().toRegularColor())
            } else {
                effect?.display(point)
            }
        }
    }

    private fun Location.getCircle(radius: Double, amount: Int): List<Location> {
        val increment = (2 * Math.PI) / amount

        val locations = mutableListOf<Location>()

        for(i in 0 until amount) {
            val angle = i * increment
            val x = clone().x + (radius * Math.cos(angle))
            val y = clone().y + 0.5
            val z = clone().z + (radius * Math.sin(angle))
            locations.add(Location(world, x, y, z))
        }

        return locations
    }
}
