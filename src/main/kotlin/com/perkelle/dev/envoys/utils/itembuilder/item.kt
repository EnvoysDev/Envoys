package com.perkelle.dev.envoys.utils.itembuilder

fun item(block : DSLItemBuilder.() -> Unit) = DSLItemBuilder().also(block).getStack()