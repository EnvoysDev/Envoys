package com.perkelle.dev.envoys.utils.gui

fun gui(rows: Int = 3, title: String? = null, block : DSLGuiBuilder.() -> Unit) = DSLGuiBuilder(rows, title).also(block).build()