package com.perkelle.dev.envoys.commands.executors

import com.perkelle.dev.envoys.config.getData
import com.perkelle.dev.envoys.config.sendFormatted
import com.perkelle.dev.envoys.holograms.HologramWrapper
import com.perkelle.dev.envoys.utils.command.CommandContext
import com.perkelle.dev.envoys.utils.command.Executor

class ClearHologramsCommand: Executor {

    override fun CommandContext.onExecute() {
        HologramWrapper.clearHolograms()

        sender sendFormatted "Cleared holograms"

        getData().save()
    }
}