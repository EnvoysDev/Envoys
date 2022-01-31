package com.perkelle.dev.envoys.envoys

import com.perkelle.dev.envoys.config.getData
import com.perkelle.dev.envoys.envoys.items.tiers.TierManager
import com.perkelle.dev.envoys.envoys.refill.Envoy
import com.perkelle.dev.envoys.envoys.refill.PredefinedEnvoy
import com.perkelle.dev.envoys.utils.blockEquals
import com.perkelle.dev.envoys.utils.config.YMLUtils
import com.perkelle.dev.envoys.utils.with
import com.perkelle.dev.envoys.utils.without
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material

class EnvoyManager {

    private val tierManager = TierManager()

    companion object {
        private val predefinedEnvoys = mutableListOf<PredefinedEnvoy>()
        private val activeEnvoys = mutableListOf<Envoy>()
        private val timeOpened = mutableMapOf<Envoy, Long>()
    }

    fun getNextID() = (predefinedEnvoys.lastOrNull()?.id ?: 0) + 1

    fun addPredefinedEnvoy(envoy: PredefinedEnvoy) {
        predefinedEnvoys.add(envoy)

        getData().config.set("predefined.${envoy.id}.tier", envoy.tier.name)
        getData().config.set("predefined.${envoy.id}.randomlocation", envoy.isRandomLocation)
        getData().config.set("predefined.${envoy.id}.chance", envoy.chance)
        getData().config.set("predefined.${envoy.id}.world", envoy.location.world?.name ?: "null")
        getData().config.set("predefined.${envoy.id}.x", envoy.location.blockX)
        getData().config.set("predefined.${envoy.id}.y", envoy.location.blockY)
        getData().config.set("predefined.${envoy.id}.z", envoy.location.blockZ)

        getData().save()
    }

    fun removePredefinedEnvoy(envoy: PredefinedEnvoy) {
        predefinedEnvoys.remove(envoy)

        getData().config.set("predefined.${envoy.id}", null)
    }

    fun getPredefinedEnvoys() = predefinedEnvoys

    fun getPredefinedEnvoyByID(id: Int) = predefinedEnvoys.firstOrNull { it.id == id }

    fun getEnvoyAtLocation(location: Location) = predefinedEnvoys.firstOrNull { it.location == location }

    fun clearActiveEnvoys() = activeEnvoys.clear()

    fun getAllActiveEnvoys() = activeEnvoys

    fun addActiveEnvoy(envoy: Envoy) {
        activeEnvoys.add(envoy)
        getData().config.set("active", getData().getList("active").with("${envoy.tier.name};${envoy.isRandomLocation};${envoy.location.world!!.name};${envoy.location.blockX};${envoy.location.blockY};${envoy.location.blockZ}"))
    }

    fun addActiveEnvoys(envoys: Collection<Envoy>) = envoys.forEach(::addActiveEnvoy)

    fun removeActiveEnvoy(envoy: Envoy) {
        activeEnvoys.remove(envoy)
        getData().config.set("active", getData().getList("active").without("${envoy.tier.name};${envoy.isRandomLocation};${envoy.location.world!!.name};${envoy.location.blockX};${envoy.location.blockY};${envoy.location.blockZ}"))
    }

    fun getActiveEnvoyAtLocation(location: Location): Envoy? {
        return activeEnvoys.firstOrNull { it.location.blockEquals(location) }
    }

    fun loadActiveEnvoys() {
        val raw = getData().getList("active", mutableListOf())
        val toRemove = mutableListOf<String>()

        for (envoyRaw in raw) {
            val split = envoyRaw.split(";")

            val tier = tierManager.getTierByName(split[0]) ?: continue
            val randomLocation = split[1] == "true"
            val world = Bukkit.getWorld(split[2]) ?: continue
            val x = split[3].toIntOrNull() ?: continue
            val y = split[4].toIntOrNull() ?: continue
            val z = split[5].toIntOrNull() ?: continue

            val loc = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
            val block = loc.block

            // Check to see if the block still exists. On crash, it may not so we don't want to load an envoy with a hologram
            if(block.type != Material.CHEST) {
                toRemove.add(envoyRaw)
                continue
            }

            activeEnvoys.add(Envoy(tier, randomLocation, loc))
        }

        activeEnvoys.forEach(Envoy::loadSavedHologram)

        val updatedList = getData().getList("active").without(toRemove)
        getData().config.set("active", updatedList)
        getData().save()
    }

    fun loadPredefinedEnvoys() {
        val envoysRaw = getData().getConfigurationSection("predefined") ?: return
        val envoys = mutableListOf<PredefinedEnvoy>()

        for (id in envoysRaw.getKeys(false)) {
            val envoy = YMLUtils(envoysRaw.getConfigurationSection(id) ?: continue)

            val tierName = envoy.getGenericOrNull<String>("tier") ?: continue
            val tier = tierManager.getTierByName(tierName) ?: continue

            val world = Bukkit.getWorld(envoy.getGenericOrNull<String>("world") ?: continue) ?: continue
            val x = envoy.getGenericOrNull<Int>("x") ?: continue
            val y = envoy.getGenericOrNull<Int>("y") ?: continue
            val z = envoy.getGenericOrNull<Int>("z") ?: continue

            val chance = envoy.getGeneric("chance", 100)

            envoys.add(
                    PredefinedEnvoy(
                            id=id.toIntOrNull() ?: continue,
                            tier=tier,
                            isRandomLocation=false,
                            location=Location(world, x.toDouble(), y.toDouble(), z.toDouble()),
                            chance=chance
                    )
            )
        }

        envoys.forEach(Envoy::loadSavedHologram)

        predefinedEnvoys.addAll(envoys)
    }

    fun removeTime(e: Envoy) = timeOpened.remove(e)

    fun clearTimes() = timeOpened.clear()

    fun setOpened(envoy: Envoy) {
        timeOpened[envoy] = System.currentTimeMillis()
    }

    fun getOpenTime(envoy: Envoy) = timeOpened[envoy]

    fun isOpened(envoy: Envoy) = getOpenTime(envoy) != null

    fun getTimeOpened() = timeOpened

    fun clearOpened() = timeOpened.clear()

    fun removeOpened(location: Location) = timeOpened.filter { (envoy, _) -> envoy.location == location }.map { it.key }.forEach { timeOpened.remove(it) }

    fun removeOpened(e: Envoy) = timeOpened.remove(e)

    fun findNearestEnvoy(location: Location): Envoy? {
        var closest: Pair<Double, Envoy>? = null

        for (envoy in activeEnvoys.filter { it.location.world == location.world }) {
            val distance = envoy.location.distanceSquared(location)
            if (closest == null || distance < closest.first) {
                closest = distance to envoy
            }
        }

        return closest?.second
    }
}