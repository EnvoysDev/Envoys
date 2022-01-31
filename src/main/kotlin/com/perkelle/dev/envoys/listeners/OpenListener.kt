package com.perkelle.dev.envoys.listeners

import com.perkelle.dev.envoys.abstraction.inventorysnapshot.IInventorySnapshot
import com.perkelle.dev.envoys.config.*
import com.perkelle.dev.envoys.envoys.EnvoyManager
import com.perkelle.dev.envoys.envoys.items.contents.EnvoyCommand
import com.perkelle.dev.envoys.particles.getParticleStyle
import com.perkelle.dev.envoys.utils.Colours
import com.perkelle.dev.envoys.utils.blockEquals
import com.perkelle.dev.envoys.utils.translateColour
import org.bukkit.Bukkit
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.ceil

class OpenListener: Listener {

    private val envoyManager = EnvoyManager()

    @EventHandler fun onOpen(e: InventoryOpenEvent) {
        val chest = e.inventory.holder as? Chest ?: return
        val p = e.player

        if(getConfig().getGeneric("tell-if-already-opened", true)) {
            envoyManager.getTimeOpened().entries.firstOrNull { it.key.location == chest.location }?.value?.let { timeOpened ->
                val minutesAgo = ceil((System.currentTimeMillis() - timeOpened) / 60000.0).toInt().toString()
                p send MessageType.ALREADY_OPENED.getMessage("%minutes" to minutesAgo)
            }
        }

        if(envoyManager.getAllActiveEnvoys().none { it.location.blockEquals(chest.location) }) return
        val envoy = envoyManager.getActiveEnvoyAtLocation(chest.location) ?: return

        envoy.hologram?.remove()

        if(envoyManager.getTimeOpened().entries.none { it.key.location == chest.location }) {
            if(getConfig().getGeneric("particles.enabled", false)) {
                getParticleStyle()?.spawnParticles(envoy.location)
            }

            val allCommands = envoy.tier.contents.mapNotNull { it as? EnvoyCommand }.toMutableList()
            val max = getConfig().getGeneric("max-commands", 5).coerceAtMost(allCommands.size)
            val min = getConfig().getGeneric("min-commands", 0).coerceAtMost(max)

            val amount = ThreadLocalRandom.current().nextInt(min, max + 1)

            val toExecute = mutableListOf<EnvoyCommand>()
            if(allCommands.size == min) toExecute.addAll(allCommands)
            else {
                while(toExecute.size != amount) {
                    val command = allCommands.random()
                    if(command.chance > ThreadLocalRandom.current().nextInt(101)) {
                        toExecute.add(command)
                        allCommands.remove(command)
                    }
                }
            }

            toExecute.forEach { envoyCommand ->
                envoyCommand.commands.forEach { cmd ->
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.translateColour()
                            .replace("%player", e.player.name)
                            .replace("%uuid", e.player.uniqueId.toString())
                            .replace("%nickname", (e.player as Player).displayName)
                            .replace("%world", chest.block.world.name)
                            .replace("%x", chest.block.location.blockX.toString())
                            .replace("%y", chest.block.location.blockY.toString())
                            .replace("%z", chest.block.location.blockZ.toString())
                            .replace("%envoy_type", envoy.tier.name)
                    )
                }
            }

            if(getConfig().getGeneric("broadcast-on-open", false)) {
                val targets =
                        if(getConfig().getGeneric("random-location.broadcast-locations.only-to-players-in-same-world", false)) Bukkit.getOnlinePlayers().filter { it.world == p.world }
                        else Bukkit.getOnlinePlayers()

                targets.forEach { target ->
                    target send MessageType.BROADCAST_OPEN.getMessage(
                            "%world" to chest.block.world.name,
                            "%x" to chest.block.x,
                            "%y" to chest.block.y,
                            "%z" to chest.block.z,
                            "%player" to e.player.name,
                            "%tier" to envoy.tier.name,
                            "%remaining" to envoyManager.getAllActiveEnvoys().size - 1
                    )
                }
            }

            if(getConfig().getGeneric("firework-on-open.enabled", true)) {
                launchFirework(envoy.location)
            }
        }

        envoyManager.setOpened(envoy)

        if(getConfig().getGeneric("destroy-on-open.enabled", false)) {
            if(getConfig().getGeneric("destroy-on-open.transfer-items-to-inventory", false)) {
                val chestInv = IInventorySnapshot.instance.getInventorySnapshot(chest)

                chest.inventory.clear()
                p.inventory.addItem(*chestInv.contents.filterNotNull().filterNot { it.type == Material.AIR }.toTypedArray())
                        .values.forEach { item -> p.world.dropItem(p.location, item) }
            }

            envoyManager.removeActiveEnvoy(envoy)
            chest.block.type = Material.AIR

            if(envoyManager.getAllActiveEnvoys().isEmpty()) {
                broadcastAllOpened(envoyManager)
            }
        }
    }

    private fun launchFirework(location: Location) {
        val power = getConfig().getGeneric("firework-on-open.power", 2)
        val flicker = getConfig().getGeneric("firework-on-open.flicker", false)
        val trail = getConfig().getGeneric("firework-on-open.trail", true)
        val type = FireworkEffect.Type.values().firstOrNull { it.name.equals(getConfig().getGeneric("firework-on-open.type", "STAR"), true) }?: FireworkEffect.Type.STAR
        val colours = getConfig().getList("firework-on-open.colors", mutableListOf()).mapNotNull { name -> Colours.values().firstOrNull { it.name.equals(name, true) } }

        val fw = location.world.spawn(location.clone().add(0.5, 1.0, 0.5), Firework::class.java)
        val fwMeta = fw.fireworkMeta
        fwMeta.power = power
        fwMeta.addEffect(FireworkEffect.builder()
                .flicker(flicker)
                .trail(trail)
                .with(type)
                .withColor(*colours.map { it.color }.toTypedArray())
                .build())
        fw.fireworkMeta = fwMeta
    }
}