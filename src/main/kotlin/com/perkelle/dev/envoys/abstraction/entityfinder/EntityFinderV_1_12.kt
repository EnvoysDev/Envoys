package com.perkelle.dev.envoys.abstraction.entityfinder

import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import java.util.*

class EntityFinderV_1_12 : IEntityFinder {
    override fun getEntityByUuid(uuid: UUID) = Bukkit.getEntity(uuid)
}
