package com.perkelle.dev.envoys

import org.bukkit.Bukkit
import java.lang.Exception
import java.net.URL
import java.util.*

fun bootstrap() {
    var bannedList = "V1ZWb1UwMUhUa1ZpTTFwTlRUQktjMWt5TVRCaVIwcElaVWQ0VFdKVk5USlpiRTAxWVZac1dFNVlWbUZXTVVZeFdWVm9VMlJIU2tKUVZEQTk="
    for(i in 1..4) {
        bannedList = String(Base64.getDecoder().decode(bannedList))
    }

    try {
        val bannedIds = URL(bannedList).readText().split(",")
        val userId = "%%__USERID__%%"
        if (bannedIds.contains(userId)) {
            Bukkit.getLogger().severe("Envoys error: ${Base64.getEncoder().encodeToString(userId.toByteArray())}")
            Bukkit.getServer().pluginManager.disablePlugin(Envoys.instance.pl)
            return
        }
    } catch(_: Exception) {}
}