package com.github.zimablue.attrsystem.utils

import com.github.zimablue.attrsystem.AttributeSystem.attributeSystemAPI
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Entity
import net.minestom.server.entity.LivingEntity
import net.minestom.server.utils.entity.EntityFinder
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

fun UUID.livingEntity() : LivingEntity? {
    MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(this)?.let {
        return it
    }
    MinecraftServer.getInstanceManager().instances.forEach { instance ->
        val entity = instance.getEntityByUuid(this)?:return@forEach
        if (entity is LivingEntity) {
            return entity
        }
    }
    return null
}