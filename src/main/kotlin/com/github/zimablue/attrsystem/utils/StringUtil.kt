package com.github.zimablue.attrsystem.utils

import com.github.zimablue.attrsystem.internal.feature.calc.FormulaParser
import com.github.zimablue.attrsystem.internal.feature.evalex.EvalEx
import com.github.zimablue.attrsystem.internal.manager.ScriptManager
import com.github.zimablue.pouplaceholder.PouPlaceholder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.minestom.server.entity.LivingEntity
import java.util.*

fun Any.toStringWithNext(): String {
    if (this is Collection<*>) {
        return this.toStringWithNext()
    }
    return this.toString()
}

fun Collection<*>.toStringWithNext(): String {
    return this.joinToString("\n")
}

fun Component.toPlain() = PlainTextComponentSerializer.plainText().serialize(this)

@JvmName("parse1")

fun String.parse(leftChar: Char = '(', rightChar: Char = ')'): List<String> {
    val text = this
    val stack = Stack<Int>()
    var left = false
    val list = ArrayList<String>()
    for (index in text.indices) {
        val char = text[index]
        if (char == leftChar) {
            if (left) {
                stack.pop()
                stack.push(index)
            } else {
                left = true
                stack.push(index)
            }
        }
        if (char == rightChar) {
            if (left) {
                val start = stack.pop()
                list.add(text.substring(start + 1 until index))
                left = false
            }
        }
    }
    return list
}

fun String.toMiniMessage(): Component {
    return net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(this)
}

fun String.placeholder(entity: LivingEntity): String {
    return PouPlaceholder.placeholderManager.replace(entity,this,"0")
}

fun String.simpleCalc() : Any {
    val result = if (startsWith("js::")) {
        ScriptManager.jsCalc(this.substring(4))
    } else if(startsWith("EvalEx::")) {
        EvalEx.eval(this.substring(8))
    } else {
        FormulaParser.calculate(this)
    }
    return result
}