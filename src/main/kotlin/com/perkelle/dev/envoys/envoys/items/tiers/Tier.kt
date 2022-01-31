package com.perkelle.dev.envoys.envoys.items.tiers

import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.envoys.items.contents.EnvoyContent
import com.perkelle.dev.envoys.envoys.items.contents.EnvoyItem
import com.perkelle.dev.envoys.utils.config.YMLUtils
import com.perkelle.dev.envoys.utils.translateColour
import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.apache.commons.math3.util.Pair
import java.util.concurrent.ThreadLocalRandom

open class Tier(val name: String, val chance: Double, val contents: List<EnvoyContent>) {

    private fun getSection() = YMLUtils(getConfig().getConfigurationSection("contents.tiers.$name")!!)

    @Suppress("UnstableApiUsage")
    fun generateItems(
        minItems: Int = getConfig().getGeneric("min-items", 1),
        maxItems: Int = getConfig().getGeneric("max-items", 5)
    ): List<EnvoyContent> {
        val allItems = contents.mapNotNull { it as? EnvoyItem }
        if (allItems.isEmpty()) return emptyList() // Some people use commands only

        val amount = ThreadLocalRandom.current().nextInt(minItems, maxItems + 1).coerceAtMost(allItems.size)
        if (amount == allItems.size) return allItems

        //val chosenItems = mutableListOf<EnvoyContent>()

        /*val totalChance = allItems.sumByDouble(EnvoyItem::chance)

        val itemsWithChance = mutableMapOf<ClosedRange<Double>, EnvoyItem>()
        allItems.forEach { item ->
            val chance = item.chance
            val lastChance = (itemsWithChance.keys.lastOrNull()?.endInclusive) ?: 0.0

            itemsWithChance[lastChance.rangeTo(lastChance + chance)] = item
        }

        while(amount > items.size) {
            val rand = ThreadLocalRandom.current().nextDouble(0.0, totalChance)
            val item = itemsWithChance.entries.first { (range, _) -> range.contains(rand) }.value

            items.add(item)
        }*/

        val chosenItems = mutableListOf<EnvoyItem>()

        val pairs = allItems.map { Pair.create(it, it.chance) }.toMutableList()
        for (i in 0 until amount) {
            val distribution = EnumeratedDistribution(pairs)
            val item = distribution.sample() as EnvoyItem
            pairs.removeIf { it.key == item }
            chosenItems.add(item)
        }

        return chosenItems
    }

    fun getHologramText() = getSection().getList("hologram-text")
        .map(String::translateColour)
        .map { s -> s.replace("%tier", name.capitalize(), true) }
}