package com.perkelle.dev.envoys.envoys.refill

import com.perkelle.dev.envoys.Envoys
import com.perkelle.dev.envoys.abstraction.ServerVersion
import com.perkelle.dev.envoys.abstraction.armourstandtick.ArmourStandTickHandler
import com.perkelle.dev.envoys.abstraction.chunkticket.IChunkTicketHandler
import com.perkelle.dev.envoys.abstraction.entityfinder.IEntityFinder
import com.perkelle.dev.envoys.abstraction.highestblock.HighestBlockHandler
import com.perkelle.dev.envoys.abstraction.safeSetInvulnerable
import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.events.EnvoySpawnEvent
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import java.lang.Exception
import java.util.*

class ArmourStandWrapper(val envoy: Envoy) {

    private var uuid: UUID? = null
    private var chunk: Chunk? = null // null if another wrapper is handling the chunk ticket

    fun spawn() {
        val spawnLocation = envoy.location.clone().add(0.5, 0.0, 0.5)
        spawnLocation.y = getConfig().getGeneric("fall-from-sky.y", 255).toDouble()

        if (IChunkTicketHandler.instance.addChunkTicket(spawnLocation.chunk, Envoys.instance.pl)) {
            chunk = spawnLocation.chunk
        }

        val armourStand = envoy.location.world?.spawnEntity(spawnLocation, EntityType.ARMOR_STAND) as ArmorStand
        armourStand.setHelmet(ItemStack(Material.CHEST))
        armourStand.isVisible = false
        armourStand.canPickupItems = false

        if (ServerVersion.version >= ServerVersion.V1_9) {
            armourStand.safeSetInvulnerable(true)
        }

        armourStand.setGravity(true)

        // bypass armor-stand-tick in Paper
        ArmourStandTickHandler.instance.setCanTick(armourStand, true)
        ArmourStandTickHandler.instance.setCanMove(armourStand, true)

        this.uuid = armourStand.uniqueId
    }

    fun startLandCheckLoop() {
        val interval = getConfig().getGeneric("fall-from-sky.check-interval", 2).toLong()

        var taskId: Int? = null
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Envoys.instance.pl, {
            val armourStand = IEntityFinder.instance.getEntityByUuid(uuid
                    ?: throw Exception("armourstand uuid is null"))

            if (armourStand == null || armourStand.isDead) {
                chunk?.let { chunk ->
                    IChunkTicketHandler.instance.removeChunkTicket(chunk, Envoys.instance.pl)
                }

                taskId?.apply { Bukkit.getScheduler().cancelTask(this) }
                return@scheduleSyncRepeatingTask
            }

            val onGround =
                    if (getConfig().getGeneric("fall-from-sky.use-alternative-on-ground-check", false)) alternativeIsOnGround(armourStand)
                    else armourStand.isOnGround

            if (onGround) {
                armourStand.remove()
                Bukkit.getServer().pluginManager.callEvent(EnvoySpawnEvent(envoy))
            }
        }, interval, interval)
    }

    private fun alternativeIsOnGround(armourStand: Entity): Boolean {
        val highestBlock = HighestBlockHandler.instance.getHighestBlockAt(armourStand.location)
        return armourStand.location.blockY - 1 <= highestBlock.y
    }
}