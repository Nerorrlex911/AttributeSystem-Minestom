package com.github.zimablue.attrsystem.utils

import com.github.zimablue.attrsystem.internal.manager.ASConfig.lang
import com.github.zimablue.devoutserver.util.colored
import net.minestom.server.command.CommandSender

fun CommandSender.sendLang(path: String, vararg replacements: Pair<String, Any>) {
    if (!lang.contains(path)) {
        sendMessage("§c[AttrSystem] §4Lang '$path' not found!")
        return
    }
    when (val value = lang[path]) {
        is String -> {
            sendMessage(value.replacement(replacements.associate { it.first to it.second }).colored())
        }

        is List<*> -> {
            value.filterIsInstance<String>().forEach { msg ->
                sendMessage(msg.replacement(replacements.associate { it.first to it.second }).colored())
            }
        }

        else -> {
            sendMessage("§c[AttrSystem] §4Lang '$path'")
        }
    }
}