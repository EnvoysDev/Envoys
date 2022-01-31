package com.perkelle.dev.envoys.api

import com.perkelle.dev.envoys.envoys.items.contents.EnvoyCommand
import com.perkelle.dev.envoys.envoys.items.contents.EnvoyItem
import com.perkelle.dev.envoys.envoys.refill.PredefinedEnvoy
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class Example: Listener {

    @EventHandler fun onEnvoySpawn(e: EnvoySpawnEvent) {
        e.isRandomLocation // True if the envoy is a random location envoy, as opposed to an envoy created through /envoys create
        e.items // List of contents in the envoy. It's mutable so you can add and remove contents as you like
        e.location // The location where the envoy will be spawned
        e.tier // The tier of the envoy
        e.isCancelled = true // Cancel the spawning of an envoy

        /**
            EnvoySpawnEvent#items returns a mutable list of EnvoyContent.
            There are classes that extend EnvoyContent, EnvoyItem and EnvoyCommand.
            An EnvoyItem represents an ItemStack that can be added to an envoy. Whereas an EnvoyCommand represents a command reward that is applied when the envoy opens.
         */
        e.items.forEach { content ->
            if(content is EnvoyItem) {
                content.getItemStack() // Get the ItemStack instance for the content
            }
            else if(content is EnvoyCommand) {
                content.commands // Returns a string list of all commands in this reward
            }
        }

        val config = EnvoysAPI.getConfig() // Returns an instance of the config wrapper
        config.save() // Save the config
        config.config // Get the YamlConfiguration instance
        config.getList(key = "contents.tiers.tier1.items", default = mutableListOf()) // Gets a list from the config
        config.getGenericOrNull<Int>(key = "config-version") // Gets a value from the config of type Int. If the value does not exist, or is invalid, null will be returned
        config.getGeneric(key = "config-version", default = 43) // Gets a value from the config of type Int. If the value does not exist, or is invalid, the default value that was specified (43) will be returned

        val data = EnvoysAPI.getData() // Returns an instance of the data file wrapper. It has the same methods as Config.

        val tierManager = EnvoysAPI.getTierManager() // Returns an instance of the tier manager
        tierManager.loadTiers() // Loads all tiers from the config, done on startup
        tierManager.clearTiers() // Clears the internal list of tiers
        tierManager.getTierByName(name = "tier1") // Gets an instance of a tier by name
        tierManager.getTiers() // Gets a list of all tiers
        val tier = tierManager.selectRandomTier() // Selects a random tier from a specified list, or defaults to all tiers

        val em = EnvoysAPI.getEnvoyManager() // Gets an instance of the envoy manager
        val nextId = em.getNextID() // Returns the next available envoy ID
        val newEnvoy = PredefinedEnvoy(id = nextId, tier = tier, isRandomLocation = false, location = Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0), chance = 100) // Creates an instance of a predefined envoy

        em.addPredefinedEnvoy(newEnvoy) // Registers a predefined envoy
        em.addActiveEnvoy(newEnvoy) // Adds an envoy to the internal list of the envoys on the ground so that other internal processes can use it
        em.clearActiveEnvoys() // Clears the list of envoys that are on the ground
        em.getActiveEnvoyAtLocation(location = Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0)) // Gets an instance of an envoy that is currently on the ground at a current location
        em.getAllActiveEnvoys() // Returns a list of all envoys that are currently on the ground
        em.getOpenTime(newEnvoy) // Returns the epoch timestamp of when an envoy was open
        em.isOpened(newEnvoy) // Whether an envoy on the ground has been opened
        em.setOpened(newEnvoy) // Registers that an envoy has been opened
        em.loadPredefinedEnvoys() // Load envoys from the config
        em.loadActiveEnvoys() // Load envoys from the data file
        em.removePredefinedEnvoy(newEnvoy) // Remove an envoy
        em.removeActiveEnvoy(newEnvoy) // Register that an envoy that was previously on the ground no longer exists

        /*val hm = EnvoysAPI.getHologramManager()
        hm.loadHolograms() // Loads holograms from the data file
        hm.encodeHologram(location = Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0), text = listOf("Hologram", "Message")) // Encodes a hologram so that it can be stored
        hm.decodeHologram(string = "") // Decodes an envoy that was stored
        hm.createHologram(loc = Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0), text = "&bAn Envoy") // Creates an envoy at a location
        hm.removeHologram(newEnvoy) // Removes hologram that belongs to an envoy
        hm.removeHologram(loc = Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0), radius = 5.0) // Remove a hologram (that was created by the Envoys plugin) in a certain radius to a location
        hm.clearHolograms() // Delete all holograms*/

        val im = EnvoysAPI.getItemManager()
        im.loadContents() // Load envoy contents from the config
        im.clearContents() // Clears the internal register of envoy contents
        im.getAllContents() // Returns a list of all possible envoy contents
        im.getContentByName(name = "my_item") // Returns an instance of an EnvoyContent by name
    }
}