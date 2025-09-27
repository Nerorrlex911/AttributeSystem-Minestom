package com.github.zimablue.attrsystem.utils

import com.github.zimablue.attrsystem.AttributeSystem.langManager
import com.github.zimablue.devoutserver.util.colored
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandSender
import net.minestom.server.command.ConsoleSender

fun console() : ConsoleSender { return MinecraftServer.getCommandManager().consoleSender }

fun CommandSender.sendLang(path: String) {
    langManager.sendLang(this, path)
}

fun CommandSender.sendLang(path: String, vararg args: Any) {
    langManager.sendLang(this, path, *args)
}

fun CommandSender.sendLang(path: String, vararg replacements: Pair<String, Any>) {
    val value = langManager.asLangText(this, path)
    value.forEach { msg ->
        sendMessage(msg.replacement(replacements.associate { it.first to it.second }).colored())
    }
}

fun ComponentLogger.langInfo(path: String, vararg args: String) {
    for (component in langManager.asLangComponent(console(), path, *args)) {
        this.info(component)
    }
}

fun ComponentLogger.langWarn(path: String, vararg args: String) {
    for (component in langManager.asLangComponent(console(), path, *args)) {
        this.warn(component)
    }
}

fun String.replaceLang(vararg args: Any): String {
    var result = this
    args.forEachIndexed { index, arg ->
        result = result.replace("{$index}", arg.toString())
    }
    return result
}