package com.github.zimablue.attrsystem.fight.internal.core.fight

import com.github.zimablue.attrsystem.fight.api.fight.DamageType
import com.github.zimablue.attrsystem.fight.api.fight.FightData
import com.github.zimablue.attrsystem.fight.api.fight.mechanic.Mechanic
import com.github.zimablue.attrsystem.internal.manager.ASConfig.debugLang
import com.github.zimablue.devoutserver.util.map.component.Keyable
import java.util.concurrent.ConcurrentHashMap


/**
 * @className MechanicData
 *
 * @author Glom
 * @date 2022/8/21 10:29 Copyright 2022 user. All rights reserved.
 */
class MechanicData(
    override val key: Mechanic,
    val type: DamageType,
    val context: ConcurrentHashMap<String, Any> = ConcurrentHashMap(),
) :
    Keyable<Mechanic>, MutableMap<String, Any> by context {
    private val mechanicKey = key.key
    fun run(fightData: FightData): Boolean {
        debugLang("fight-info-mechanic", mechanicKey)
        val result = key.run(fightData, this, type)
        if (!fightData.hasResult) return false
        debugLang("fight-info-mechanic-return", result.toString())
        result?.let { fightData[mechanicKey] = it }
        return true
    }

    fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "mechanic" to mechanicKey,
            "context" to this
        )
    }

    fun clone(): MechanicData {
        return MechanicData(key, type, ConcurrentHashMap(context))
    }
}