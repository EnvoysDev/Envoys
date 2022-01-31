package com.perkelle.dev.envoys.listeners

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatListener: Listener {

    companion object {
        val callbacks = mutableMapOf<Player, (AsyncPlayerChatEvent) -> Unit>()
    }

    @EventHandler fun onChat(e: AsyncPlayerChatEvent) {
        val p = e.player
        val callback = callbacks[p] ?: return
        callbacks.remove(p, callback)
        callback(e)
    }
}