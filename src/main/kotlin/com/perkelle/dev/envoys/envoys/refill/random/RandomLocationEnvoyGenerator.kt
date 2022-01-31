package com.perkelle.dev.envoys.envoys.refill.random

import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.envoys.items.tiers.Tier
import com.perkelle.dev.envoys.envoys.items.tiers.TierManager
import com.perkelle.dev.envoys.envoys.refill.Envoy
import com.perkelle.dev.envoys.integrations.IntegrationProvider
import com.perkelle.dev.envoys.utils.*
import com.perkelle.dev.envoys.utils.config.YMLUtils
import com.perkelle.dev.envoys.worldguard.Vector2D
import com.perkelle.dev.envoys.worldguard.WorldGuardUtil
import com.perkelle.dev.envoys.worldguard.getRegionByName
import com.perkelle.dev.envoys.worldguard.isInRegion
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.codemc.worldguardwrapper.region.IWrappedRegion
import java.util.concurrent.ThreadLocalRandom

class RandomLocationEnvoyGenerator {

    private val tierManager = TierManager()

    fun generateRandomLocationEnvoys(amount: Int = getConfig().getGeneric("random-location.amount", 0)): List<Envoy> {
        // Get 'random-location' config section and create a wrapper for it
        val randomLocationSection = getConfig().getConfigurationSection("random-location")
        if (randomLocationSection == null) {
            Bukkit.getLogger().warning("Could not find random-location configuration")
            return emptyList()
        }

        val randomLocation = YMLUtils(randomLocationSection)

        // Check whether we will be using WorldGuard integration
        val useWorldGuard = getConfig().getGeneric("random-location.worldguard-integration.enabled", false)
        val worldGuardType = WorldGuardIntegrationType.values().firstOrNull {
            it.configName.equals(getConfig().getGeneric("random-location.worldguard-integration.type", "everywhere"), true)
        }

        // Check that the user has specified a valid WorldGuard integration type
        if (useWorldGuard && worldGuardType == null) {
            Bukkit.getLogger().warning("Invalid WorldGuard type. Should be everywhere / blacklist / whitelist.")
            return emptyList()
        }

        // Initiate list of Envoys
        val envoys = mutableListOf<Envoy>()

        // Check that we're not using whitelist mode (as it requires another type of location generation)
        if (!useWorldGuard || worldGuardType == WorldGuardIntegrationType.EVERYWHERE || worldGuardType == WorldGuardIntegrationType.BLACKLIST) {
            val disabledWorlds = randomLocation.getList("disabled-worlds").mapNotNull(Bukkit::getWorld)

            // Generate envoy locations for each world, apart from disabled worlds
            for (world in Bukkit.getWorlds().without(disabledWorlds)) {
                // Get list of blacklisted regions
                val blacklistedRegions =
                        if(WorldGuardUtil.isInstalled()) randomLocation.getList("worldguard-integration.blacklisted-regions").mapNotNull(world::getRegionByName)
                        else emptyList()

                for (i in 0 until amount) {
                    val dontSpawnInAir = randomLocation.getGeneric("dont-spawn-in-air", false)
                    val maxScans =
                            if (dontSpawnInAir) 20
                            else 1

                    var envoyLocation: Location? = null
                    var counter = 0

                    while (envoyLocation == null && counter < maxScans) {
                        counter++ // Increment counter to prevent infinite scan loop in a void region, or something

                        // Pick an X and Z coordinate
                        val x = ThreadLocalRandom.current().nextInt(
                                randomLocation.getGeneric("min-x", -10000),
                                randomLocation.getGeneric("max-x", 10000)
                        )
                        val z = ThreadLocalRandom.current().nextInt(
                                randomLocation.getGeneric("min-z", -10000),
                                randomLocation.getGeneric("max-z", 10000)
                        )

                        val (minY, maxY) = getMinMaxY(randomLocation)

                        // Pick a Y coordinate
                        // If the user wants always-on-top, we must iterate down from maxY to minY and find the first empty block
                        // If the user does not, we can just pick a random Y coordinate
                        val y = if (randomLocation.getGeneric("always-on-top", true)) {
                            val maxBlock = world.getHighestBlock(x, minY, maxY, z)
                            if (dontSpawnInAir && maxBlock?.type == Material.AIR) {
                                continue
                            } else {
                                // Default to minY if there are no empty blocks - rather have an underground envoy than an envoy floating in the sky
                                val blockY = maxBlock?.y ?: minY
                                blockY + 1
                            }
                        } else {
                            ThreadLocalRandom.current().nextInt(minY, maxY + 1)
                        }

                        envoyLocation = Location(world, x.toDouble(), y.toDouble(), z.toDouble())

                        // Check for worldguard blacklist
                        if (useWorldGuard && worldGuardType == WorldGuardIntegrationType.BLACKLIST) {
                            if (blacklistedRegions.any { it.contains(envoyLocation) }) {
                                envoyLocation = null
                            }
                        }
                    }

                    // Make sure location is not null
                    envoyLocation ?: continue

                    // Create the envoy
                    val randomTiers = randomLocation.getList("random-tiers").mapNotNull { tierManager.getTierByName(it) }
                    val envoy = Envoy(tierManager.selectRandomTier(randomTiers), true, envoyLocation)
                    envoys.add(envoy)
                }
            }
        }
        // Whitelist WorldGuard integration requires a different location generation method
        else if (useWorldGuard && worldGuardType == WorldGuardIntegrationType.WHITELIST) {
            // Retrieve WorldGuard integration config section
            val regionsRaw = YMLUtils(randomLocation.getConfigurationSection("worldguard-integration.whitelisted-regions") ?: return emptyList())
            val regions = mutableListOf<WhitelistedRegion>()

            // Create a list of all regions and their properties
            for (regionName in regionsRaw.config.getKeys(false)) {
                val region = YMLUtils(regionsRaw.getConfigurationSection(regionName) ?: continue)

                // Get the world object, skip to next cycle if it is null
                val world = Bukkit.getWorld(regionsRaw.getGenericOrNull<String>("$regionName.world") ?: continue)
                        ?: continue

                // Get manual tiers & amounts
                val manual = mutableMapOf<Tier, Int>()
                region.getConfigurationSection("tiers")?.let { manualSection ->
                    val tierNames = manualSection.getKeys(false)
                    tierNames.forEach { tierName ->
                        tierManager.getTierByName(tierName)?.let { tier ->
                            val amount = manualSection.getInt(tierName, 0)
                            manual[tier] = amount
                        }
                    }
                }

                regions.add(WhitelistedRegion(
                        world,
                        world.getRegionByName(regionName) ?: continue,
                        region.getGeneric("tier-selection", "automatic"),
                        manual,
                        region.getGeneric("amount", 0),
                        region.getList("exclude")
                ))
            }

            // Create envoy locations
            for (region in regions) {
                val max = WorldGuardUtil.getMax(region.region)
                val min = WorldGuardUtil.getMin(region.region)

                if (region.tierSelection == "manual") {
                    region.tiers.forEach { (tier, amount) ->
                        for (i in 0 until amount) {
                            val location = region.generateLocation(min, max, randomLocation) ?: continue

                            val envoy = Envoy(tier, true, location)
                            envoys.add(envoy)
                        }
                    }
                } else { // Use automatic by default
                    for (i in 0 until region.amount) {
                        val location = region.generateLocation(min, max, randomLocation) ?: continue

                        val tier = tierManager.selectRandomTier(randomLocation.getList("random-tiers").mapNotNull { tierManager.getTierByName(it) })
                        val envoy = Envoy(tier, true, location)
                        envoys.add(envoy)
                    }
                }
            }
        }

        return envoys
    }

    private fun WhitelistedRegion.generateLocation(min: Vector2D, max: Vector2D, randomLocation: YMLUtils): Location? {
        val excludedRegions = exclusions.mapNotNull { WorldGuardUtil.getRegion(world, it) }

        val dontSpawnInAir = randomLocation.getGeneric("dont-spawn-in-air", false)
        val maxScans =
                if (dontSpawnInAir) 20
                else 1

        var envoyLocation: Location? = null
        var counter = 0

        while (envoyLocation == null && counter < maxScans) {
            counter++

            val x = ThreadLocalRandom.current().nextInt(min.x, max.x)
            val z = ThreadLocalRandom.current().nextInt(min.z, max.z)

            val (minY, maxY) = getMinMaxY(randomLocation)

            // Pick a Y coordinate
            // If the user wants always-on-top, we must iterate down from maxY to minY and find the first empty block
            // If the user does not, we can just pick a random Y coordinate
            val y = if (randomLocation.getGeneric("always-on-top", true)) {
                val maxBlock = world.getHighestBlock(x, minY, maxY, z)
                if (dontSpawnInAir && maxBlock?.type == Material.AIR) {
                    continue
                }

                // Default to minY if there are no empty blocks - rather have an underground envoy than an envoy floating in the sky
                val blockY = maxBlock?.y ?: minY
                blockY + 1
            } else {
                ThreadLocalRandom.current().nextInt(minY, maxY + 1)
            }

            envoyLocation = Location(world, x.toDouble(), y.toDouble(), z.toDouble())

            // Check excluded regions
            if(excludedRegions.any(envoyLocation::isInRegion) || !IntegrationProvider.canPlace(envoyLocation)) {
                envoyLocation = null
                continue
            }
        }

        return envoyLocation
    }

    data class MinMaxY(val min: Int, val max: Int)

    fun getMinMaxY(randomLocation: YMLUtils): MinMaxY {
        // Select a Y coordinate between the minimum and maximum values the user has supplied.
        // Ensure minY and maxY 0 <= y <= 255
        var minY = randomLocation.getGeneric("minimum-y", 0).assertInRange(0, 255)
        var maxY = randomLocation.getGeneric("maximum-y", 255).assertInRange(0, 255)

        // Ensure that maxY > minY
        minY = minY.assertLessThanOrEqual(maxY)
        maxY = maxY.assertGreaterThanOrEqual(minY)

        return MinMaxY(minY, maxY)
    }

    data class WhitelistedRegion(
            val world: World,
            val region: IWrappedRegion,
            val tierSelection: String = "automatic",
            val tiers: Map<Tier, Int> = emptyMap(),
            val amount: Int = 0,
            val exclusions: List<String> = emptyList()
    )
}