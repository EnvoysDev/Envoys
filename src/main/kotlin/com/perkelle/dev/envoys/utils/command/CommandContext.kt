package com.perkelle.dev.envoys.utils.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

data class CommandContext(val sender: CommandSender, val player: Player?, val command: String, val args: Array<String>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommandContext

        if (sender != other.sender) return false
        if (player != other.player) return false
        if (command != other.command) return false
        if (!Arrays.equals(args, other.args)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sender.hashCode()
        result = 31 * result + (player?.hashCode() ?: 0)
        result = 31 * result + command.hashCode()
        result = 31 * result + Arrays.hashCode(args)
        return result
    }
}