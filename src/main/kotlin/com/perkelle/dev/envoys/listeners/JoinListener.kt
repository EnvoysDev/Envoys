package com.perkelle.dev.envoys.listeners

import com.perkelle.dev.envoys.Envoys
import com.perkelle.dev.envoys.VersionManager
import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.config.sendFormatted
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinListener : Listener {

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val p = e.player

        val notificationsEnabled = getConfig().getGeneric("update-checker.notify-on-join", true)
        if (notificationsEnabled && p.isOp) {
            val current = Envoys.instance.pl.description.version

            VersionManager.getLatestVersion { version ->
                if (version != null && version != current) {
                    p.sendFormatted("A new version of Envoys is available! It is recommended that you update ASAP.")
                    p.sendFormatted("You are running: v$current")
                    p.sendFormatted("Latest version: v$version")
                }
            }
        }
    }
}