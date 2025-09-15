package com.github.zimablue.attrsystem.fight.internal.core.fight

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.fight.api.event.MechanicLoadEvent
import com.github.zimablue.attrsystem.fight.api.fight.DamageType
import com.github.zimablue.attrsystem.fight.api.fight.FightData
import com.github.zimablue.attrsystem.fight.api.fight.mechanic.Mechanic
import com.github.zimablue.attrsystem.utils.langInfo
import com.github.zimablue.devoutserver.util.map.component.Keyable
import taboolib.library.configuration.ConfigurationSection
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.optionals.getOrNull

/**
 * Mechanic data
 *
 * @constructor Create empty Mechanic data
 * @property key 伤害类型
 * @property enable 是否启用(字符串类型，会被解析)
 */
class MechanicDataCompound private constructor(
    override val key: DamageType,
    val enable: String,
) : Keyable<DamageType>{
    val process = ArrayList<MechanicData>()

    companion object {
        private fun loadMechanicEvent(key:String): Mechanic? {
            val event = MechanicLoadEvent(key)
            AttributeSystem.asEventNode.call(event)
            return event.mechanic.getOrNull()?.apply { register() }
        }

        @JvmStatic
        fun deserialize(section: ConfigurationSection): MechanicDataCompound? {
            val damageType = AttributeSystem.damageTypeManager[section.name]
            damageType ?: kotlin.run {
                AttributeSystem.logger.langInfo("invalid-damage-type", section.toString())
                return null
            }
            val compound = MechanicDataCompound(damageType, section.getString("enable") ?: "true")
            if (section.contains("mechanics")) {
                val mechanics = section.getList("mechanics") ?: return compound
                for (context in mechanics) {
                    context as? MutableMap<String, Any>? ?: continue
                    val key = context["mechanic"].toString()
                    val machine = AttributeSystem.mechanicManager[key] ?: loadMechanicEvent(key)
                    if (machine == null) {
                        AttributeSystem.logger.langInfo("invalid-mechanic", "${section.name}.$key")
                        continue
                    }
                    compound.process += MechanicData(machine, damageType, ConcurrentHashMap(context))
                }
                return compound
            }
            for (key in section.getKeys(false)) {
                if (key == "enable") continue
                val machine = AttributeSystem.mechanicManager[key]
                if (machine == null) {
                    AttributeSystem.logger.langInfo("invalid-mechanic", "${section.name}.$key")
                    continue
                }
                compound.process += MechanicData(
                    machine,
                    damageType,
                    ConcurrentHashMap<String,Any>().apply {
                        section.getConfigurationSection(key)!!.toMap().forEach { (key, value) ->
                            this[key] = value?:return@forEach
                        }
                    }
                )
            }
            return compound
        }
    }

    /**
     * Run
     *
     * @param fightData
     */
    fun run(fightData: FightData): Boolean {
        if (!fightData.handleStr(enable).toBoolean().also {
                fightData["enable"] = it
            }) return false
        for (data in process) if (!data.run(fightData)) break
        return true
    }

    fun serialize(): MutableMap<String, Any> {
        val map = LinkedHashMap<String, Any>()
        map["enable"] = enable
        map["mechanics"] = process.map { it.serialize() }
        return map
    }


}
