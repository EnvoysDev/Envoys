package com.perkelle.dev.envoys.utils

import com.google.common.reflect.ClassPath
import com.perkelle.dev.envoys.Envoys
import com.perkelle.dev.envoys.envoys.items.contents.ContentsManager
import com.perkelle.dev.envoys.envoys.items.tiers.TierManager
import com.perkelle.dev.envoys.utils.command.ICommand
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.SimpleCommandMap
import org.bukkit.event.Listener
import org.mozilla.javascript.Context
import org.mozilla.javascript.NativeJavaObject
import org.mozilla.javascript.ScriptableObject

private var cmdMap: SimpleCommandMap? = null

private fun getCommandMap(): SimpleCommandMap {
    return if(cmdMap != null) cmdMap!!
    else {
        val map = Bukkit.getServer()::class.java.getDeclaredField("commandMap")
        map.isAccessible = true
        cmdMap = map.get(Bukkit.getServer()) as SimpleCommandMap
        cmdMap!!
    }
}

fun registerListeners(loader: ClassLoader, packageName: String) {
    val classPath = ClassPath.from(loader)
    classPath.getTopLevelClassesRecursive(packageName).map(ClassPath.ClassInfo::load).filter { Listener::class.java.isAssignableFrom(it) }.forEach {
        Bukkit.getServer().pluginManager.registerEvents(it.newInstance() as Listener, Envoys.instance.pl)
    }
}

fun registerCommand(cmd: Command) {
    getCommandMap().register(cmd.name, cmd)
}

fun registerCommands(loader: ClassLoader, packageName: String) {
    val classPath = ClassPath.from(loader)
    classPath.getTopLevelClassesRecursive(packageName).map { it.load() }.filter { it.interfaces.contains(ICommand::class.java) }.forEach {
        (it.newInstance() as ICommand).register()
    }
}
