package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.api.AttrAPI.getASHealth
import com.github.zimablue.attrsystem.api.AttrAPI.getASMaxHealth
import com.github.zimablue.attrsystem.api.AttrAPI.setASHealth
import com.github.zimablue.attrsystem.api.event.HealthRegainEvent
import com.github.zimablue.attrsystem.fight.api.FightAPI.isFighting
import com.github.zimablue.attrsystem.utils.simpleCalc
import com.github.zimablue.pouplaceholder.PouPlaceholder
import net.minestom.server.entity.LivingEntity
import taboolib.common5.cdouble
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
        return PouPlaceholder.placeholderManager.replace(entity, value,"0").simpleCalc().cdouble
    }

    fun regain(entity: LivingEntity) {
        if(entity.aliveTicks%period != 0L) return
        if(!enable) return
        if(disableInFighting && entity.isFighting()) return
        val maxHealth = entity.getASMaxHealth()
        val health = entity.getASHealth()
        if (health >= maxHealth) return
        val amount = getValue(entity)
        val event = HealthRegainEvent(entity, amount)
        if (event.isCancelled) return
        val result = min(maxHealth, health + event.amount)
        entity.setASHealth(result)
    }
}