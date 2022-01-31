@file:JvmName("Command")
package com.perkelle.dev.envoys.utils.command

fun command(name: String, block: CommandBuilder.() -> Unit) = CommandBuilder(name).also(block)