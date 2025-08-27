package com.github.zimablue.attrsystem.utils

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.minestom.server.component.DataComponents
import net.minestom.server.item.ItemStack

fun ItemStack.hasLore(str: String): Boolean {
    val lore = get(DataComponents.LORE) ?: return false
    lore.forEach {
        if(PlainTextComponentSerializer.plainText().serialize(it).contains(str)) {
            return true
        }
    }
    return false
}