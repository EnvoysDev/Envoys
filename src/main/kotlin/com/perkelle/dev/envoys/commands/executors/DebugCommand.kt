package com.perkelle.dev.envoys.commands.executors

import com.perkelle.dev.envoys.Envoys
import com.perkelle.dev.envoys.abstraction.ServerVersion
import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.config.sendFormatted
import com.perkelle.dev.envoys.utils.command.CommandContext
import com.perkelle.dev.envoys.utils.command.Executor
import com.perkelle.dev.envoys.worldguard.WorldGuardUtil
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.plugin.Plugin
import org.json.JSONObject
import java.io.File
import java.lang.Exception
import java.net.URL

class DebugCommand: Executor {

    override fun CommandContext.onExecute() {
        val sb = StringBuilder()

        sb.append("Registered to: https://www.spigotmc.org/members/%%__USERID__%%/")
        sb.blank(2)

        sb.append("Plugins: ${Bukkit.getServer().pluginManager.plugins.joinToString(", ", transform = Plugin::getName)}\n")
        sb.blank(2)

        sb.append("Worlds: ${Bukkit.getWorlds().joinToString(", ", transform = World::getName)}\n")
        sb.blank(2)

        sb.append("MC version: ${Bukkit.getVersion()} (${ServerVersion.version.name})\n")
        sb.append("Bukkit version: ${Bukkit.getBukkitVersion()}\n")
        sb.append("Envoys version: ${Envoys.instance.pl.description.version}\n")
        sb.blank(2)

        sb.append("Worldguard: \n")
        sb.append("Is installed: ${WorldGuardUtil.isInstalled()}\n")
        if(WorldGuardUtil.isInstalled()) sb.append("Regions: ${WorldGuardUtil.getRegions()}")
        sb.blank(2)

        sb.append("Config: \n")
        sb.append(getConfig().dump())
        sb.blank(2)

        val logFolder = File(Bukkit.getWorldContainer(), "logs")
        val logFile = File(logFolder, "latest.log")
        val log = logFile.readLines().joinToString("\n")
        sb.append("Log: \n")
        sb.append(log)

        val debug = sb.toString()

        Bukkit.getScheduler().runTaskAsynchronously(Envoys.instance.pl, Runnable {
            try {
                val res = Request.Post("https://hb.wrmsr.io/documents")
                        .addHeader("Content-Type", "text/plain")
                        .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0")
                        .bodyString(debug, ContentType.TEXT_PLAIN)
                        .execute()

                val key = JSONObject(res.returnContent().asString()).getString("key")

                val debugUrl = URL("https://hb.wrmsr.io/$key")
                sender sendFormatted "The debug URL is $debugUrl"
            } catch(ex: Exception) {
                Bukkit.getLogger().warning("An error occurred:")
                ex.printStackTrace()
            }
        })
    }

    private fun StringBuilder.blank(amount: Int = 1) {
        repeat(amount) {
            append("\n")
        }
    }
}