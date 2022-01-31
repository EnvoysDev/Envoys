package com.perkelle.dev.envoys.commands.executors

import com.perkelle.dev.envoys.Envoys
import com.perkelle.dev.envoys.config.MessageType
import com.perkelle.dev.envoys.config.getConfig
import com.perkelle.dev.envoys.config.sendMessage
import com.perkelle.dev.envoys.envoys.items.contents.ContentsManager
import com.perkelle.dev.envoys.envoys.items.tiers.TierManager
import com.perkelle.dev.envoys.utils.command.CommandContext
import com.perkelle.dev.envoys.utils.command.Executor

class ReloadCommand: Executor {

    private val contentsManager = ContentsManager()
    private val tierManager = TierManager()

    override fun CommandContext.onExecute() {
        // Possible fix for issue where reload command isn't reloading?
        val cfg = getConfig()
        cfg.reload() // Refresh config
        Envoys.instance.config = cfg

        // Refresh contents
        contentsManager.clearContents()
        contentsManager.loadContents()

        // Refresh tiers
        tierManager.clearTiers()
        tierManager.loadTiers()

        sender sendMessage MessageType.RELOAD
    }
}