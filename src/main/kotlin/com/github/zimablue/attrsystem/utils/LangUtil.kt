package com.github.zimablue.attrsystem.utils

import com.github.zimablue.attrsystem.internal.manager.ASConfig.lang
import com.github.zimablue.devoutserver.util.colored
import net.minestom.server.command.CommandSender
import org.slf4j.Logger



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

fun Logger.langInfo(path: String, vararg args: String) {
    if (!lang.contains(path)) {
        info("§c[AttrSystem] §4Lang '$path' not found!")
        return
    }
    when (val value = lang[path]) {
        is String -> {
            info("§c[AttrSystem] §e" + value.colored(),*args)
        }

        is List<*> -> {
            info("§c[AttrSystem] §e------------------")
            value.filterIsInstance<String>().forEach { msg ->
                info("§c[AttrSystem] §e" + msg.colored(),*args)
            }
            info("§c[AttrSystem] §e------------------")
        }

        else -> {
            info("§c[AttrSystem] §4Lang '$path'")
        }
    }
}

fun Logger.langWarn(path: String, vararg args: String) {
    if (!lang.contains(path)) {
        warn("§c[AttrSystem] §4Lang '$path' not found!")
        return
    }
    when (val value = lang[path]) {
        is String -> {
            warn("§c[AttrSystem] §e" + value.colored(),*args)
        }

        is List<*> -> {
            warn("§c[AttrSystem] §e------------------")
            value.filterIsInstance<String>().forEach { msg ->
                warn("§c[AttrSystem] §e" + msg.colored(),*args)
            }
            warn("§c[AttrSystem] §e------------------")
        }

        else -> {
            warn("§c[AttrSystem] §4Lang '$path'")
        }
    }
}