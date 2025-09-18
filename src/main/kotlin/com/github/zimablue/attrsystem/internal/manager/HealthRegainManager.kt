package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.api.event.HealthRegainEvent
import com.github.zimablue.attrsystem.fight.api.FightAPI.isFighting
import com.github.zimablue.attrsystem.utils.simpleCalc
import com.github.zimablue.pouplaceholder.PouPlaceholder
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.attribute.Attribute
import taboolib.common5.cdouble
import taboolib.common5.cfloat
import kotlin.math.min

object HealthRegainManager {
    val enable: Boolean
        get() = ASConfig.options.getBoolean("health-regain.enable", false)
    val period: Long
        get() = ASConfig.options.getLong("health-regain.period",10)
    val disableInFighting: Boolean
        get() = ASConfig.options.getBoolean("health-regain.disable-in-fighting", false)
    val value: String
        get() = ASConfig.options.getString("health-regain.value") ?: "1"

    fun getValue(entity: LivingEntity): Double {
        return PouPlaceholder.placeholderManager.replace(entity, value).simpleCalc().cdouble
    }

    fun regain(entity: LivingEntity) {
        if(entity.aliveTicks%period != 0L) return
        if(!enable) return
        if(disableInFighting && entity.isFighting()) return
        val maxHealth = entity.getAttributeValue(Attribute.MAX_HEALTH)
        val health = entity.health
        if (health >= maxHealth) return
        val amount = getValue(entity)
        val event = HealthRegainEvent(entity, amount)
        if (event.isCancelled) return
        val result = min(maxHealth, health.cdouble + event.amount)
        entity.health = result.cfloat
    }
}