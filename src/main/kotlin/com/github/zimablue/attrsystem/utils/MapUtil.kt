package com.github.zimablue.attrsystem.utils

import com.github.zimablue.attrsystem.internal.feature.calc.FormulaParser.calculate
import com.github.zimablue.attrsystem.internal.manager.ScriptManager
import net.minestom.server.entity.LivingEntity
import taboolib.library.configuration.ConfigurationSection
import java.util.ArrayList
import java.util.HashMap

internal fun <T : Any> T.clone(): Any {
    return when (this) {
        is Map<*, *> -> {
            val map = HashMap<String, Any>()
            forEach { (key, value) ->
                key ?: return@forEach
                value ?: return@forEach
                map[key.toString()] = value.clone()
            }
            map
        }

        is List<*> -> {
            val list = ArrayList<Any>()
            mapNotNull { it }.forEach {
                list.add(it.clone())
            }
            list
        }

        else -> this
    }
}

internal fun <T : Any> T.replaceThenCalc(replacement: Map<String, String>, entity: LivingEntity?): Any {
    return when (this) {
        is Map<*, *> -> {
            val map = HashMap<String, Any>()
            forEach { (key, value) ->
                key ?: return@forEach
                value ?: return@forEach
                map[key.toString()] = value.replaceThenCalc(replacement, entity)
            }
            map
        }

        is List<*> -> {
            val list = ArrayList<Any>()
            mapNotNull { it }.forEach {
                list.add(it.replaceThenCalc(replacement, entity))
            }
            list
        }

        is String -> {
            val replaced = replacement(replacement)
            if (replaced.startsWith("js::")) ScriptManager.jsCalc(replaced) else calculate(replaced)
        }

        else -> this
    }
}

fun String.replacement(replaces: Map<String, Any>): String {
    var formulaCopy = this
    replaces.forEach {
        formulaCopy = formulaCopy.replace(it.key, it.value.toString())
    }
    return formulaCopy
}
