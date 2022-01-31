@file:JvmName("KtUtils")

package com.perkelle.dev.envoys.utils

import com.perkelle.dev.envoys.Envoys
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import java.util.*

fun<T> MutableCollection<T>.with(vararg elements: T): Collection<T> {
    addAll(elements)
    return this
}

fun<T> MutableCollection<T>.without(vararg elements: T): Collection<T> {
    removeAll(elements)
    return this
}

fun<T> MutableCollection<T>.without(elements: Collection<T>): Collection<T> {
    removeAll(elements)
    return this
}


inline fun<reified T> Array<out T>.with(vararg elements: T) = arrayOf(*this, *elements)

fun String.translateColour() = ChatColor.translateAlternateColorCodes('&', this)
fun String.stripColour() = ChatColor.stripColor(this)
fun String.translateAndStrip() = translateColour().stripColour()

fun String.isInt() = none { !it.isDigit() }

fun async(block: () -> Unit) = Bukkit.getScheduler().runTaskAsynchronously(Envoys.instance.pl, block)
fun sync(block: () -> Unit) = Bukkit.getScheduler().runTask(Envoys.instance.pl, block)
fun async(delay: Long = 0, block: () -> Unit) = Bukkit.getScheduler().runTaskLaterAsynchronously(Envoys.instance.pl, block, delay)
fun sync(delay: Long = 0, block: () -> Unit) = Bukkit.getScheduler().runTaskLater(Envoys.instance.pl, block, delay)

fun Location.blockEquals(compareTo: Location) = world == compareTo.world && blockX == compareTo.blockX && blockY == compareTo.blockY && blockZ == compareTo.blockZ

fun Location.log() = println("${world?.name ?: "null"} $blockX : $blockY : $blockZ")

infix fun String.equalsIgnoreCase(compare: String) = equals(compare, true)

fun World.getHighestBlock(x: Int, minY: Int, maxY: Int, z: Int): Block? {
    for(y in maxY downTo minY){
        val block = getBlockAt(x, y, z)
        if(!block.isEmpty) {
            return block
        }
    }
    return null
}

/**
 * Checks that the integer is in the range @param min - @param max, inclusive of both values, and returns @param default if it is not
 */
fun Int.assertInRange(min: Int, max: Int, default: Int = min): Int {
    return if(!IntRange(min, max).contains(this)) {
        default
    } else {
        this
    }
}

fun Int.assertGreaterThanOrEqual(compareTo: Int, default: Int = compareTo): Int {
    return if(this < compareTo) {
        default
    } else {
        this
    }
}

fun runLater(ticks: Long, block: () -> Unit) = Bukkit.getScheduler().runTaskLater(Envoys.instance.pl, block, ticks)

fun Int.assertLessThanOrEqual(compareTo: Int, default: Int = compareTo): Int {
    return if(this > compareTo) {
        default
    } else {
        this
    }
}

fun String.base64Encode() = Base64.getEncoder().encodeToString(this.toByteArray())
fun String.base64Decode() = Base64.getDecoder().decode(this).toString(Charsets.UTF_8)
