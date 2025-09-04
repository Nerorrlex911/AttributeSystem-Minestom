package com.github.zimablue.attrsystem.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

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