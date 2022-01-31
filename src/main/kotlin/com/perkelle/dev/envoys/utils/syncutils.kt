package com.perkelle.dev.envoys.utils

import com.perkelle.dev.envoys.Envoys
import org.bukkit.Bukkit

fun<T, U> async(block: () -> T, callback: ((T) -> U)? = null) {
    Bukkit.getScheduler().runTaskAsynchronously(Envoys.instance.pl, Runnable {
        val res = block()

        callback?.let { callback ->
            Bukkit.getScheduler().runTask(Envoys.instance.pl, Runnable {
                callback(res)
            })
        }
    })
}

