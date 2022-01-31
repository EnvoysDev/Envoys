package com.perkelle.dev.envoys.abstraction.chunkticket

import org.bukkit.Chunk
import org.bukkit.plugin.Plugin

class ChunkTicketHandlerV_1_14 : IChunkTicketHandler {
    override fun addChunkTicket(chunk: Chunk, plugin: Plugin) = false
    override fun removeChunkTicket(chunk: Chunk, plugin: Plugin) = false
}