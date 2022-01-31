package com.perkelle.dev.envoys.envoys.items.tiers

import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.envoys.items.contents.ContentsManager
import com.perkelle.dev.envoys.envoys.items.contents.EnvoyContent
import com.perkelle.dev.envoys.utils.config.YMLUtils
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import java.util.concurrent.ThreadLocalRandom

class TierManager {

    private val contentsManager = ContentsManager()

    companion object {
        private val tiers = mutableListOf<Tier>()
    }

    fun loadTiers() {
        val tierSection = getConfig().getConfigurationSection("contents.tiers")
        val tierNames = tierSection?.getKeys(false) ?: listOf<String>()

        for(tierName in tierNames) {
            val tier = YMLUtils(tierSection?.getConfigurationSection(tierName) ?: continue) // Impossible for tier section to be null

            val chance = tier.getGenericOrNull<Double>("chance") ?: tier.getGenericOrNull<Int>("chance")?.toDouble()
            if(chance == null) {
                Bukkit.getLogger().warning("Invalid tier chance")
                continue
            }

            val contentsRaw = tier.getListOrNull("items")
            if(contentsRaw == null) {
                Bukkit.getLogger().warning("Invalid tier contents")
                continue
            }

            val contents = mutableListOf<EnvoyContent>()
            contentsRaw.forEach { name ->
                val content = contentsManager.getContentByName(name)

                if(content == null)
                    Bukkit.getLogger().warning("Invalid item name: $name")
                else {
                    contents.add(content)
                }
            }

            tiers.add(Tier(tierName, chance, contents))
        }
    }

    fun selectRandomTier(availableTiers: List<Tier> = tiers): Tier {
        val chancesMap = mutableMapOf<ClosedFloatingPointRange<Double>, Tier>()
        var current = 0.0

        availableTiers.forEach {
            chancesMap[current.rangeTo(current + it.chance)] = it
            current += it.chance
        }

        val r = ThreadLocalRandom.current().nextDouble(0.0, current)
        return chancesMap.entries.first { it.key.contains(r) }.value
    }

    fun getTierByName(name: String) = tiers.firstOrNull { it.name.equals(name, true) }

    fun getTiers() = tiers

    fun clearTiers() = tiers.clear()
}