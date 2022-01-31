package com.perkelle.dev.envoys.abstraction.armourstandtick

import org.bukkit.entity.ArmorStand

class ArmourStandTickHandlerPaper : ArmourStandTickHandler {
    override fun canTick(armourStand: ArmorStand) = armourStand.canTick()
    override fun setCanTick(armourStand: ArmorStand, canTick: Boolean) = armourStand.setCanTick(canTick)
    override fun canMove(armourStand: ArmorStand) = armourStand.canMove()
    override fun setCanMove(armourStand: ArmorStand, canMove: Boolean) = armourStand.setCanMove(canMove)
}