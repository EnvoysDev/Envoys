package com.perkelle.dev.envoys.abstraction.armourstandtick

import com.perkelle.dev.envoys.ServerType
import org.bukkit.entity.ArmorStand

interface ArmourStandTickHandler {
    fun canTick(armourStand: ArmorStand): Boolean
    fun setCanTick(armourStand: ArmorStand, canTick: Boolean)

    fun canMove(armourStand: ArmorStand): Boolean
    fun setCanMove(armourStand: ArmorStand, canMove: Boolean)

    companion object {
        val instance:  ArmourStandTickHandler by lazy {
            when(ServerType.type) {
                ServerType.PAPER -> ArmourStandTickHandlerPaper()
                ServerType.SPIGOT -> ArmourStandTickHandlerSpigot()
            }
        }
    }
}