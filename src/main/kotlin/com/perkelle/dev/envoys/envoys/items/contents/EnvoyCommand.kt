package com.perkelle.dev.envoys.envoys.items.contents

// More of a "Commands Package" tbh
class EnvoyCommand(chance: Double, name: String, val commands: List<String>): EnvoyContent(ContentType.COMMAND, name, chance)