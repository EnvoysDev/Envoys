package com.perkelle.dev.envoys.abstraction.armourstandtick

import org.bukkit.entity.ArmorStand

class ArmourStandTickHandlerSpigot : ArmourStandTickHandler {
    override fun canTick(armourStand: ArmorStand) = true
    override fun setCanTick(armourStand: ArmorStand, canTick: Boolean) {}
    override fun canMove(armourStand: ArmorStand) = true
    override fun setCanMove(armourStand: ArmorStand, canMove: Boolean) {}
}