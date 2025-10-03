package com.github.zimablue.attrsystem.fight.api.fight

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.api.AttrAPI.attribute
import com.github.zimablue.attrsystem.api.AttrAPI.getAttrData
import com.github.zimablue.attrsystem.api.AttrAPI.hasData
import com.github.zimablue.attrsystem.api.attribute.compound.AttributeDataCompound
import com.github.zimablue.attrsystem.internal.feature.compat.placeholder.AttributePlaceHolder
import com.github.zimablue.attrsystem.internal.manager.ASConfig
import com.github.zimablue.attrsystem.utils.checkName
import com.github.zimablue.attrsystem.utils.langWarn
import net.minestom.server.component.DataComponents
import net.minestom.server.entity.LivingEntity
import java.util.concurrent.ConcurrentHashMap

/**
 * @className DataCache
 *
 * @author Glom
 * @date 2023/1/22 18:00 Copyright 2023 user. All rights reserved.
 */
class DataCache(data: FightData? = null) {
    var data: FightData? = data
        set(value) {
            field = value
            field ?: return
            field!!["attacker-name"] = attackerName
            field!!["defender-name"] = defenderName
        }
    var attackerData: AttributeDataCompound? = null
    var defenderData: AttributeDataCompound? = null
    var attackerName: String = ASConfig.defaultAttackerName
    var defenderName: String = ASConfig.defaultDefenderName
    val variables = ConcurrentHashMap<String, Any>()

    fun setData(other: DataCache) {
        other.attackerData?.let { attackerData = it }
        other.defenderData?.let { defenderData = it }
    }

    fun attacker(entity: LivingEntity?): DataCache {
        entity ?: return this
        if (!entity.hasData())
            AttributeSystem.attributeSystemAPI.update(entity)
        attackerData = entity.getAttrData()!!.clone()
        attackerName = entity.checkName()
        data ?: return this
        data!!["attacker"] = entity
        data!!["attacker-name"] = attackerName
        return this
    }

    fun defender(entity: LivingEntity?): DataCache {
        entity ?: return this
        if (!entity.hasData())
            AttributeSystem.attributeSystemAPI.update(entity)
        defenderData = entity.getAttrData()!!.clone()
        defenderName = entity.checkName()
        data ?: return this
        data!!["defender"] = entity
        data!!["defender-name"] = defenderName
        return this
    }

    fun attackerData(attKey: String, params: List<String>): String {
        val attribute = attribute(attKey)
        attribute ?: AttributeSystem.logger.langWarn("invalid-attribute", attKey)
        attribute ?: return "0.0"
        return attackerData?.let { AttributePlaceHolder.get(it, attribute, params) } ?: "0.0"
    }

    fun defenderData(attKey: String, params: List<String>): String {
        val attribute = attribute(attKey)
        attribute ?: AttributeSystem.logger.langWarn("invalid-attribute", attKey)
        attribute ?: return "0.0"
        return defenderData?.let { AttributePlaceHolder.get(it, attribute, params) } ?: "0.0"
    }
    
    fun variables(vars: Map<String, Any>): DataCache {
        variables.putAll(vars)
        return this
    }
}