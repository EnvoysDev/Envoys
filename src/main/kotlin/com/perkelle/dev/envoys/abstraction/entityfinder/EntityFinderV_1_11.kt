package com.perkelle.dev.envoys.abstraction.entityfinder

import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import java.util.*

class EntityFinderV_1_11 : IEntityFinder {
    override fun getEntityByUuid(uuid: UUID): Entity? {
        for(world in Bukkit.getWorlds()) {
            world.entities.firstOrNull { it.uniqueId == uuid }?.let { entity ->
                return entity
            }
        }

        return null
    }
}
