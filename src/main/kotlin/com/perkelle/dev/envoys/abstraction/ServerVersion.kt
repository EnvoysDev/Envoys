package com.perkelle.dev.envoys.abstraction

import org.bukkit.Bukkit

enum class ServerVersion {
    UNKNOWN,
    V1_8,
    V1_9,
    V1_10,
    V1_11,
    V1_12,
    V1_13,
    V1_14,
    V1_15,
    V1_16,
    V1_17,
    V1_18,
    V1_19,
    ;

    companion object {
        private val versionRegex = Regex("(?:MC: (\\d*.\\d*))")

        /*val version: ServerVersion by lazy {
            val parsed = versionRegex.find(Bukkit.getVersion())?.groups?.get(1)?.value ?: return@lazy UNKNOWN
            values().firstOrNull { it.name == "V" + parsed.replace(".", "_")  } ?: UNKNOWN
        }*/

        private var inner: ServerVersion? = null
        val version: ServerVersion
            get() {
                inner?.let { return it }

                val parsed = versionRegex.find(Bukkit.getVersion())?.groups?.get(1)?.value
                if (parsed == null) {
                    inner = UNKNOWN
                    return UNKNOWN
                }

                inner = values().firstOrNull { it.name == "V" + parsed.replace(".", "_")  } ?: UNKNOWN
                return inner!!
            }
    }
}
