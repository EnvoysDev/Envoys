package com.perkelle.dev.envoys.holograms

import org.bukkit.Bukkit

fun isHolographicDisplaysInstalled() = Bukkit.getServer().pluginManager.isPluginEnabled("HolographicDisplays")