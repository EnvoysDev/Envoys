package com.perkelle.dev.envoys

enum class ServerType {
    SPIGOT,
    PAPER
    ;

    companion object {
        val type: ServerType by lazy {
            when {
                isPaper() -> PAPER
                else -> SPIGOT
            }
        }

        private fun isPaper(): Boolean {
            return try {
                Class.forName("com.destroystokyo.paper.VersionHistoryManager\$VersionData")
                true
            } catch(_: ClassNotFoundException) {
                false
            }
        }
    }
}