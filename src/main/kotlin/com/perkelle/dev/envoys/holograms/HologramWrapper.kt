package com.perkelle.dev.envoys.holograms

import com.gmail.filoghost.holographicdisplays.api.Hologram
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import com.perkelle.dev.envoys.Envoys
import com.perkelle.dev.envoys.config.getData
import com.perkelle.dev.envoys.utils.with
import com.perkelle.dev.envoys.utils.without
import org.bukkit.Bukkit
import org.bukkit.Location

class HologramWrapper(val loc: Location, val text: List<String>) {

    private var hologram: Hologram? = null

    fun spawn() {
        // Create empty hologram
        hologram = HologramsAPI.createHologram(Envoys.instance.pl, loc)
        hologram?.clearLines()

        // Add lines to hologram
        text.withIndex().forEach { (index, line) ->
            hologram?.insertTextLine(index, line)
        }

        // Save to data file
        getData().config.set("holograms", getData().getList("holograms", mutableListOf()).with(encode()))
        getData().save()
    }

    fun remove() {
        hologram?.delete()

        var holograms = getData().getList("holograms", mutableListOf())
        holograms = holograms.without(encode()).toMutableList()

        getData().config.set("holograms", holograms)
        getData().save()
    }

    // Encode hologram such that it can be stored in a YML file as a single string
    fun encode(): String {
        return  "${loc.world?.name};${loc.x};${loc.y};${loc.z};${text.joinToString(";")}"
    }

    companion object {
        fun decode(encoded: String): HologramWrapper {
            val split = encoded.split(";")

            val world = Bukkit.getWorld(split[0])
            val x = split[1].toDouble()
            val y = split[2].toDouble()
            val z = split[3].toDouble()

            val lines = split.subList(4, split.size)

            return HologramWrapper(Location(world, x, y, z), lines)
        }

        fun clearHolograms() {
            if(!isHolographicDisplaysInstalled()) return

            HologramsAPI.getHolograms(Envoys.instance.pl).forEach(Hologram::delete)

            getData().config.set("holograms", null)
            getData().save()
        }
    }
}