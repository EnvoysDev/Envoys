package com.perkelle.dev.envoys.api

import com.perkelle.dev.envoys.envoys.items.contents.EnvoyContent
import com.perkelle.dev.envoys.envoys.items.tiers.Tier
import org.bukkit.Location
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class EnvoySpawnEvent(val location: Location, val tier: Tier, var items: List<EnvoyContent>, val isRandomLocation: Boolean): Event(), Cancellable {

    private var cancel = false

    companion object {
        @JvmStatic val handlerList = HandlerList()
    }

    override fun getHandlers() = handlerList

    override fun isCancelled() = cancel
    override fun setCancelled(cancel: Boolean) {
        this.cancel = cancel
    }
}