package com.perkelle.dev.envoys.worldguard

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.codemc.worldguardwrapper.WorldGuardWrapper
import org.codemc.worldguardwrapper.region.IWrappedRegion
import org.codemc.worldguardwrapper.selection.ICuboidSelection

fun Location.isInRegion(region: IWrappedRegion) = region.contains(this)

fun World.getRegionByName(name: String) = WorldGuardWrapper.getInstance().getRegion(this, name).orElse(null)

object WorldGuardUtil {

    fun isInstalled() = Bukkit.getServer().pluginManager.getPlugin("WorldGuard") != null

    fun getMax(region: IWrappedRegion): Vector2D {
        val selection = region.selection as ICuboidSelection
        return Vector2D(selection.maximumPoint.blockX, selection.maximumPoint.blockZ)
    }

    fun getMin(region: IWrappedRegion): Vector2D {
        val selection = region.selection as ICuboidSelection
        return Vector2D(selection.minimumPoint.blockX, selection.minimumPoint.blockZ)
    }

    fun getRegions(): String {
        val sb = StringBuilder()

        Bukkit.getWorlds().forEach { world ->
            WorldGuardWrapper.getInstance().getRegions(world).forEach { name, _ ->
                sb.append("[$name], ${world.name}")
            }
        }

        return sb.toString()
    }

    fun getRegion(world: World, region: String): IWrappedRegion? = WorldGuardWrapper.getInstance().getRegion(world, region).orElse(null)

    fun register(pl: Plugin) {
        WorldGuardWrapper.getInstance().registerEvents(pl as JavaPlugin)
    }
}
