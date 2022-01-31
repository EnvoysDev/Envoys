package com.perkelle.dev.envoys.api

import com.perkelle.dev.envoys.Envoys
import com.perkelle.dev.envoys.envoys.EnvoyManager
import com.perkelle.dev.envoys.envoys.items.contents.ContentsManager
import com.perkelle.dev.envoys.envoys.items.tiers.TierManager
import com.perkelle.dev.envoys.getEnvoysCore

class EnvoysAPI {

    companion object {
        private val envoyManager = EnvoyManager()
        private val itemManager = ContentsManager()
        private val tierManager = TierManager()

        @JvmStatic fun getEnvoyManager() = envoyManager
        @JvmStatic fun getItemManager() = itemManager
        @JvmStatic fun getTierManager() = tierManager

        @JvmStatic fun getConfig() = getEnvoysCore().config
        @JvmStatic fun getData() = getEnvoysCore().data

        @JvmStatic fun getNextRefill() = Envoys.nextRefill
    }
}