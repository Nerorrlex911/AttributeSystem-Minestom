package com.github.zimablue.attrsystem.api.compiled

import com.github.zimablue.devoutserver.util.map.KeyMap
import com.github.zimablue.attrsystem.api.condition.Condition
import com.github.zimablue.attrsystem.api.condition.ConditionData
import net.minestom.server.entity.LivingEntity

/**
 * @className CompiledData
 *
 * @author Glom
 * @date 2023/8/2 18:51 Copyright 2023 user. All rights reserved.
 */
abstract class CompiledData : KeyMap<Condition, ConditionData>(), Evalable {


    fun putAllCond(other: KeyMap<Condition, ConditionData>) {
        other.forEach { (condition, conditionData) ->
            computeIfAbsent(condition) { ConditionData(condition) }.addAll(conditionData)
        }
    }

    open fun putAll(other: CompiledData) {
        putAllCond(other)
    }

    open fun condition(entity: LivingEntity?): Boolean {
        return values.all { data ->
            data.condition(entity)
        }
    }

    open fun serialize(): MutableMap<String, Any> {
        val total = LinkedHashMap<String, Any>()
        values.forEach {
            total.putAll(it.serialize())
        }
        return total
    }


}