package com.perkelle.dev.envoys.envoys.items.contents

open class EnvoyContent(val type: ContentType, val name: String, val chance: Double) {

    @Suppress("UNCHECKED_CAST")
    internal fun<T> getAs() = this as T
}