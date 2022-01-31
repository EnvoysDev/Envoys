package com.perkelle.dev.envoys.abstraction.chunkticket

import com.perkelle.dev.envoys.abstraction.ServerVersion
import org.bukkit.Chunk
import org.bukkit.plugin.Plugin

interface IChunkTicketHandler {
    fun addChunkTicket(chunk: Chunk, plugin: Plugin): Boolean
    fun removeChunkTicket(chunk: Chunk, plugin: Plugin): Boolean

    companion object {
        val instance: IChunkTicketHandler by lazy {
            if (ServerVersion.version >= ServerVersion.V1_15) {
                ChunkTicketHandlerV_1_15()
            } else {
                ChunkTicketHandlerV_1_14()
            }
        }
    }
}