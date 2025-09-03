package com.github.zimablue.attrsystem.utils

fun Any.toStringWithNext(): String {
    if (this is Collection<*>) {
        return this.toStringWithNext()
    }
    return this.toString()
}

fun Collection<*>.toStringWithNext(): String {
    return this.joinToString("\n")
}