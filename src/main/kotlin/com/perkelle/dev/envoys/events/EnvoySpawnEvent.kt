package com.perkelle.dev.envoys.events

import com.perkelle.dev.envoys.envoys.refill.Envoy
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class EnvoySpawnEvent(val envoy: Envoy) : Event() {

    companion object {
        @JvmField
        val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList() = handlerList
    }

    override fun getHandlers() = handlerList
}