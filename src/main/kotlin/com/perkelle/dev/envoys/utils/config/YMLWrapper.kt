package com.perkelle.dev.envoys.utils.config

import com.google.common.io.ByteStreams
import com.perkelle.dev.envoys.Envoys
import com.perkelle.dev.envoys.config.getConfig
import org.bukkit.Bukkit
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.mozilla.javascript.Context
import org.mozilla.javascript.NativeJavaArray
import org.mozilla.javascript.NativeJavaObject
import org.mozilla.javascript.ScriptableObject
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.net.URL
import java.nio.channels.ShutdownChannelGroupException

abstract class YMLWrapper(val folder: File? = Envoys.instance.pl.dataFolder) {

    private lateinit var file: File
    lateinit var config: YamlConfiguration
    lateinit var utils: YMLUtils

    @Throws(IOException::class)
    fun load(): Boolean {
        folder?.let {
            if(!it.exists()) it.mkdir()
        }

        val fileName = javaClass.getAnnotation(FileName::class.java).fileName

        file = if(folder == null) File(fileName)
        else File(folder, fileName)

        if(!file.exists()) {
            createFromResource(file, fileName)
        }

        config = YamlConfiguration.loadConfiguration(file)

        file.save()

        utils = YMLUtils(config)

        file.save()
        utils = YMLUtils(config)
        return true
    }

    @Throws(IOException::class)
    fun File.save() {
        config.save(this)
    }

    @Throws(IOException::class)
    fun save() {
        file.save()
    }

    fun dump(): String = file.readLines().joinToString("\n")

    @Throws(IOException::class)
    fun createFromResource(file: File, resourceName: String) {
        file.createNewFile()
        val inStream = Envoys::class.java.classLoader.getResourceAsStream(resourceName)
        val outStream = file.outputStream()

        ByteStreams.copy(inStream, outStream)

        inStream.close()
        outStream.close()
    }

    fun reload() {
        config = YamlConfiguration.loadConfiguration(file)
        utils = YMLUtils(config)
    }

    inline fun <reified T> getGeneric(key: String, default: T) = utils.getGeneric(key, default)

    inline fun <reified T> getGenericOrNull(key: String) = utils.getGenericOrNull<T>(key)

    fun getConfigurationSection(key: String) = utils.getConfigurationSection(key)

    fun getList(key: String, default: MutableList<String>) = utils.getList(key, default)
    fun getList(key: String) = utils.getList(key, mutableListOf())

    fun getListOrNull(key: String) = utils.getListOrNull(key)

    private fun shutdownInvalidConfig(ex: Exception) {
        Bukkit.getLogger().severe("-------------------------------------------------------")
        Bukkit.getLogger().severe("Envoys encountered an issue whilst reading your config!")
        Bukkit.getLogger().severe("Shutting down to prevent any adverse affects!")
        Bukkit.getLogger().severe("Discord support server: https://discord.gg/3Bv4FQn")
        Bukkit.getLogger().severe("-------------------------------------------------------")
        ex.printStackTrace()

        Bukkit.getServer().pluginManager.disablePlugin(Envoys.instance.pl)
    }
}