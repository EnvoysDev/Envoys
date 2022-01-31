package com.perkelle.dev.envoys.utils.nbt

data class NBTObject(val type: NBTType, val key: String, val value: Any) {
    inline fun<reified T> convertValue(): T {
        if(!type.supported)
            throw IllegalArgumentException("Unsupported NBT data type: ${type.configName}")

        return value as T
    }
}