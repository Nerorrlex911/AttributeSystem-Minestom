package com.github.zimablue.attrsystem.utils

import com.github.zimablue.attrsystem.AttributeSystem.attributeSystemAPI
import com.github.zimablue.pouplaceholder.util.livingEntity
import net.minestom.server.entity.Entity
import net.minestom.server.entity.LivingEntity
import java.util.*

fun UUID.validEntity(): LivingEntity? {

    return livingEntity() ?: run {
        attributeSystemAPI.remove(this)
        null
    }
}

fun Entity.isAlive(): Boolean {
    return isLiving(this)
}

fun isLiving(entity: Entity?) : Boolean {
    return entity is LivingEntity && !entity.isDead && entity.isActive
}