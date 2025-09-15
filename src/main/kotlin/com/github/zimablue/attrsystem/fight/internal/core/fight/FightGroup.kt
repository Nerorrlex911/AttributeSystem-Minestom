package com.github.zimablue.attrsystem.fight.internal.core.fight

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.fight.api.event.DamageTypeRunEvent
import com.github.zimablue.attrsystem.fight.api.fight.DamageType
import com.github.zimablue.attrsystem.fight.api.fight.FightData
import com.github.zimablue.attrsystem.internal.manager.ASConfig.debug
import com.github.zimablue.attrsystem.internal.manager.ASConfig.debugLang
import com.github.zimablue.devoutserver.util.map.LinkedKeyMap
import com.github.zimablue.devoutserver.util.map.component.Registrable
import taboolib.library.configuration.ConfigurationSection
import java.util.function.Function

/**
 * Fight group
 *
 * @constructor Create empty Fight group
 * @property key 战斗组键
 */
class FightGroup(
    override val key: String
) : Registrable<String>,
    LinkedKeyMap<DamageType, MechanicDataCompound>() {

    /** Damage types */
    val damageTypes = this.list

    /**
     * Run
     *
     * @param originData 原战斗数据
     * @return
     */
    internal fun run(originData: FightData): Function<FightData, Double> {
        debugLang("fight-info")
        debugLang("fight-info-key", key)
        debugLang("fight-info-attacker", originData["attacker-name"].toString())
        debugLang("fight-info-defender", originData["defender-name"].toString())

        for (index in damageTypes.indices) {
            val type = damageTypes[index]
            debugLang("fight-info-damage-type", type.name)
            var fightData = FightData(originData)
            val before = DamageTypeRunEvent.Pre(type, fightData, false)
            AttributeSystem.asEventNode.call(before)
            fightData = before.fightData
            if (!(this[type]!!.run(fightData) || before.enable)) continue
            val post = DamageTypeRunEvent.Post(type, fightData)
            AttributeSystem.asEventNode.call(post)
            fightData = post.fightData
            val result = fightData.calResult()
            fightData["result"] = result
            if (originData.damageTypes.containsKey(type)) {
                originData.damageTypes[type]!!.apply {
                    putAll(fightData)
                    damageSources.putAll(fightData.damageSources)
                }
            }

            originData.damageTypes[type] = fightData
            debugLang("fight-info-usable-vars")
            debug {
                fightData.forEach {
                    if (it.key.startsWith("type::")) return@forEach
                    if (it.value::class.java.simpleName.contains("Function", true)) return@forEach
                    debug("      type::${type.key}-${it.key} : ${it.value}")
                    originData["type::${type.key}-${it.key}"] = it.value
                }
            }
        }
        return Function {
            val result = it.calResult()
            debugLang("fight-info-result", result.toString())
            result
        }
    }

    fun serialize(): MutableMap<String, Any> {
        val map = LinkedHashMap<String, Any>()
        for (damageType in damageTypes) {
            map[damageType.key] = this[damageType]?.serialize() ?: continue
        }
        return map
    }

    companion object {
        @JvmStatic
        fun deserialize(section: ConfigurationSection): FightGroup? {
            val key = section.name
            val fightGroup = FightGroup(key)
            for (damageTypeKey in section.getKeys(false)) {
                val damageType = AttributeSystem.damageTypeManager[damageTypeKey] ?: continue
                fightGroup[damageType] =
                    MechanicDataCompound.deserialize(section.getConfigurationSection(damageTypeKey)!!) ?: continue
            }
            return fightGroup
        }
    }

    override fun register() {
        AttributeSystem.fightGroupManager.register(this)
    }
}
