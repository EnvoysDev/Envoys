package com.perkelle.dev.envoys.utils.nbt

enum class NBTType(val configName: String, val supported: Boolean = true) {
    END("END", false),
    BYTE("BYTE"),
    SHORT("SHORT"),
    INT("INT"),
    LONG("LONG"),
    FLOAT("FLOAT"),
    DOUBLE("DOUBLE"),
    BYTE_ARRAY("BYTE_ARRAY", false),
    STRING("STRING"),
    LIST("LIST", false),
    COMPOUND("COMPOUND", false),
    INT_ARRAY("INT_ARRAY", false),
    LONG_ARRAY("LONG_ARRAY", false),
}