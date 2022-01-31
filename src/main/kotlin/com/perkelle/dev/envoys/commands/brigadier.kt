package com.perkelle.dev.envoys.commands

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.perkelle.dev.envoys.envoys.items.tiers.TierManager

internal fun getCommandNode(root: String, tierManager: TierManager) = LiteralArgumentBuilder.literal<Any>(root)

        .then(LiteralArgumentBuilder.literal<Any>("additem")
                .then(RequiredArgumentBuilder.argument<Any, String>("name", StringArgumentType.word())
                        .then(RequiredArgumentBuilder.argument("chance", DoubleArgumentType.doubleArg(0.0, 100.0)))))

        .then(LiteralArgumentBuilder.literal("amount"))
        .then(LiteralArgumentBuilder.literal("clearholograms"))
        .then(LiteralArgumentBuilder.literal("compass"))

        .then(LiteralArgumentBuilder.literal<Any>("create").withTiers(tierManager) { then(RequiredArgumentBuilder.argument("chance", DoubleArgumentType.doubleArg(0.0, 100.0))) })

        .then(LiteralArgumentBuilder.literal("current"))
        .then(LiteralArgumentBuilder.literal("debug"))
        .then(LiteralArgumentBuilder.literal("edit"))

        .then(LiteralArgumentBuilder.literal<Any>("giveflare")
                .then(RequiredArgumentBuilder.argument("player", StringArgumentType.word()))
                .withTiers(tierManager) { then(RequiredArgumentBuilder.argument("amount", IntegerArgumentType.integer(1, 64))) })

        .then(LiteralArgumentBuilder.literal("list"))
        .then(LiteralArgumentBuilder.literal("refill"))
        .then(LiteralArgumentBuilder.literal("reload"))

        .then(LiteralArgumentBuilder.literal<Any>("remove")
                .then(RequiredArgumentBuilder.argument("id", IntegerArgumentType.integer())))

        .then(LiteralArgumentBuilder.literal("savedata"))
        .then(LiteralArgumentBuilder.literal<Any>("single")
                .then(RequiredArgumentBuilder.argument("world", StringArgumentType.word()))
                .then(RequiredArgumentBuilder.argument("x", IntegerArgumentType.integer()))
                .then(RequiredArgumentBuilder.argument("y", IntegerArgumentType.integer()))
                .then(RequiredArgumentBuilder.argument("z", IntegerArgumentType.integer()))
                .then(RequiredArgumentBuilder.argument("tier", StringArgumentType.word()))
                .then(RequiredArgumentBuilder.argument("delete_after", IntegerArgumentType.integer(0)))
        )

        .then(LiteralArgumentBuilder.literal("stop"))
        .then(LiteralArgumentBuilder.literal("clear"))

        .build()

fun LiteralArgumentBuilder<Any>.withTiers(tierManager: TierManager, also: LiteralArgumentBuilder<Any>.() -> Unit): LiteralArgumentBuilder<Any> {
    tierManager.getTiers().forEach { tier ->
        then(RequiredArgumentBuilder.argument("tier", StringArgumentType.word())).also(also)
    }

    return this
}