package com.perkelle.dev.envoys.abstraction.chunkticket

import org.bukkit.Chunk
import org.bukkit.plugin.Plugin

class ChunkTicketHandlerV_1_15 : IChunkTicketHandler {
    override fun addChunkTicket(chunk: Chunk, plugin: Plugin) = chunk.addPluginChunkTicket(plugin)
    override fun removeChunkTicket(chunk: Chunk, plugin: Plugin) = chunk.removePluginChunkTicket(plugin)
}