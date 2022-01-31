package com.perkelle.dev.envoys.config

import com.perkelle.dev.envoys.utils.translateColour
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

enum class MessageType(val configName: String, val default: String, val inLang: Boolean = true) {
    PREFIX("prefix", "&8[&4Envoys&8] &4"),
    NO_PERMISSION("no-permission", "You do not have permission for this"),
    SETUP_FINISHED("setup-finished", "Envoy setup successfully"),
    ID_INVALID("not-a-valid-id", "That ID does not exist / is not valid"),
    REMOVED_ENVOY("removed-envoy", "Successfully removed envoy"),
    REFILLED("envoys-refilled", "ALL ENVOYS HAVE BEEN REFILLED"),
    AMOUNT("amount", "There are %amount envoys (%worldAmount in your world)"),
    LIST("list", "%id - World: %world X: %x Y: %y Z: %z"),
    ENVOY_SPAWNED("random-envoy-locations", "An envoy spawned at World: %world X: %x Y: %y Z: %z!"),
    RELOAD("reload", "Reloaded successfully"),
    DATA_SAVED("data-saved", "Data file saved successfully"),
    INVALID_SYNTAX("invalid-syntax", "Invalid syntax, type /envoys help for help"),
    NO_NAME_OR_CHANCE("add-item-no-name-or-chance", "Invalid syntax, type /envoys help for help"),
    NO_ITEM_IN_HAND("no-item-in-hand", "You must have an item in your hand"),
    NAME_ALREADY_EXISTS("name-already-exists", "This name already exists in the config"),
    ADDED_ITEM("added-item", "Successfully added item"),
    BROADCAST_OPEN("broadcast-open-message", "%player opened an envoy at %world, %x, %y, %z"),
    CALLED_DROP("called-drop", "You have called a drop in!"),
    CURRENT_ENVOYS_TOP("current-envoys.top", "Current envoys:"),
    CURRENT_ENVOYS_LINE("current-envoys.line", "There is an %tier envoy at: %world, %x, %y, %z"),
    ON_DROP_PLACE("drop-item.broadcast-on-place", "%player called an envoy in at %x, %y, %z", inLang = false),
    ALREADY_OPENED("already-opened", "This envoy was already opened %minutes minutes ago!"),
    DISABLED_REGION("disabled-region", "Envoy drops cannot be called in this area"),
    COMPASS_DISABLED("compass-disabled", "Envoy compass tracking has been disabled"),
    COMPASS_ENABLED("compass-enabled", "Envoy compass tracking has been enabled"),
    COMPASS_ENVOY_DISTANCE("compass-envoy-distance", "The nearest envoy is %distance metres away"),
    COMPASS_NO_ACTIVE_ENVOYS("compass-no-active-envoys", "There are no envoys currently active"),
    CLEARED_ENVOYS("cleared-envoys", "Cleared all envoys"),
    ALL_ENVOYS_OPENED("all-envoys-opened", "All envoys have been opened!"),
    LIST_EMPTY("envoy-list-empty", "You haven't created any predefined envoys yet. Use /envoy create to create some")
}

fun MessageType.getMessage(vararg placeholders: Pair<String, Any>): String? {
    val key =
            if (inLang) "lang.$configName"
            else configName

    var msg = getConfig().getGeneric(key, default)
    if (msg.isEmpty()) return null

    if (this == MessageType.PREFIX) return msg

    placeholders.forEach {
        msg = msg.replace(it.first, it.second.toString())
    }

    return ChatColor.translateAlternateColorCodes('&', getConfig().getGeneric("lang.prefix", MessageType.PREFIX.default) + msg)
}

infix fun CommandSender.send(str: String?) = str?.let {
    sendMessage(str)
}

infix fun CommandSender.sendMessage(msgType: MessageType) = msgType.getMessage()?.apply { sendMessage(this) }

infix fun CommandSender.sendFormatted(s: String?) = s?.apply { sendMessage((getConfig().getGeneric("lang.prefix", MessageType.PREFIX.default) + this).translateColour()) }
