package com.perkelle.dev.envoys.envoys.items

import com.perkelle.dev.envoys.envoys.items.contents.EnvoyItem

fun envoyItem(chance: Double, name: String, block: EnvoyItem.() -> Unit) = EnvoyItem(chance, name).also(block)