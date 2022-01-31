package com.perkelle.dev.envoys.abstraction.entityfinder

import com.perkelle.dev.envoys.abstraction.ServerVersion
import org.bukkit.entity.Entity
import java.util.*

interface IEntityFinder {
    fun getEntityByUuid(uuid: UUID): Entity?

    companion object {
        val instance: IEntityFinder by lazy {
            if(ServerVersion.version >= ServerVersion.V1_12) {
                EntityFinderV_1_12()
            } else {
                EntityFinderV_1_11()
            }
        }
    }
}