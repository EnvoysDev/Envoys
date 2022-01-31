package com.perkelle.dev.envoys.listeners

import com.perkelle.dev.envoys.config.MessageType
import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.config.getMessage
import com.perkelle.dev.envoys.envoys.EnvoyManager
import com.perkelle.dev.envoys.utils.config.YMLUtils
import com.perkelle.dev.envoys.utils.without
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player

fun broadcastAllOpened(envoyManager: EnvoyManager) {
    // Make announcement if there are no more remaining envoys
    if(envoyManager.getAllActiveEnvoys().isEmpty()) {
        val msg = MessageType.ALL_ENVOYS_OPENED.getMessage()
        if(msg != null) {
            // Make a list of players to broadcast the message to
            val players = mutableListOf<Player>()
            if(getConfig().getGeneric("random-location.broadcast-locations.only-to-players-in-same-world", false)) {
                val worlds = mutableListOf<World>()
                // If we're using whitelist WG mode then we must only add the worlds in which the regions are
                if(getConfig().getGeneric("random-location.worldguard-integration.enabled", false) && getConfig().getGeneric("random-location.worldguard-integration.type", "everywhere") == "whitelist") {
                    val whitelistSection = YMLUtils(getConfig().getConfigurationSection("random-location.worldguard-integration.whitelisted-regions") ?: return)

                    // Parse each region in the config to get the world and add it to the list of worlds
                    whitelistSection.config.getKeys(false).forEach { key ->
                        val section = whitelistSection.getConfigurationSection(key)
                        val worldName = section?.getString("world") ?: return@forEach
                        val world = Bukkit.getWorld(worldName)
                        if(world != null) {
                            worlds.add(world)
                        }
                    }
                } else { // If we're not using worldguard (or using blacklist mode), then we can just get all worlds and remove the disabled-worlds worlds
                    val disabledWorlds = getConfig().getList("random-location.disabled-worlds").mapNotNull(Bukkit::getWorld)
                    val enabledWorlds = Bukkit.getWorlds().without(disabledWorlds)
                    worlds.addAll(enabledWorlds)
                }

                players.addAll(worlds.map(World::getPlayers).flatten())
            }

            // Now we have got a list of players, we can broadcast the message to them
            players.forEach { p ->
                p.sendMessage(msg)
            }
        }
    }
}