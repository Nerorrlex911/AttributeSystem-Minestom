package com.github.zimablue.attrsystem.utils

import com.github.zimablue.attrsystem.internal.manager.ASConfig.lang
import com.github.zimablue.devoutserver.util.colored
import net.minestom.server.command.CommandSender
import org.slf4j.Logger

fun CommandSender.sendLang(path: String, vararg args: Any) {
    if (!lang.contains(path)) {
        sendMessage("§c[AttrSystem] §4Lang '$path' not found!")
        return
    }
    when (val value = lang[path]) {
        is String -> {
            sendMessage(value.replaceLang(*args).colored())
        }

        is List<*> -> {
            sendMessage("§c[AttrSystem] §e------------------")
            value.filterIsInstance<String>().forEach { msg ->
                sendMessage(msg.replaceLang(*args).colored())
            }
            sendMessage("§c[AttrSystem] §e------------------")
        }

        else -> {
            sendMessage("§c[AttrSystem] §4Lang '$path'")
        }
    }
}

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
            info("§c[AttrSystem] §e" + value.replaceLang(*args).colored())
        }

        is List<*> -> {
            info("§c[AttrSystem] §e------------------")
            value.filterIsInstance<String>().forEach { msg ->
                info("§c[AttrSystem] §e" + msg.replaceLang(*args).colored())
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
            warn("§c[AttrSystem] §e" + value.replaceLang(*args).colored())
        }

        is List<*> -> {
            warn("§c[AttrSystem] §e------------------")
            value.filterIsInstance<String>().forEach { msg ->
                warn("§c[AttrSystem] §e" + msg.replaceLang(*args).colored())
            }
            warn("§c[AttrSystem] §e------------------")
        }

        else -> {
            warn("§c[AttrSystem] §4Lang '$path'")
        }
    }
}

fun String.replaceLang(vararg args: Any): String {
    var result = this
    args.forEachIndexed { index, arg ->
        result = result.replace("{$index}", arg.toString())
    }
    return result
}