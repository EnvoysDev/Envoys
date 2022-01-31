package com.perkelle.dev.envoys.abstraction

import com.perkelle.dev.envoys.utils.XMaterial
import org.bukkit.entity.ArmorStand

fun ArmorStand.safeSetInvulnerable(invulnerable: Boolean) {
    if(ServerVersion.version < ServerVersion.V1_9) {
        throw IllegalAccessException("Armour stands can only be made invulnerable on versions greater than or equal to 1.9")
    }

    this.isInvulnerable = invulnerable
}

