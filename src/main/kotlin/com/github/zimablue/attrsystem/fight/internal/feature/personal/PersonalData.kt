package com.github.zimablue.attrsystem.fight.internal.feature.personal

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.internal.manager.ASConfig
import com.github.zimablue.devoutserver.util.map.component.Registrable
import java.util.*

class PersonalData(override val key: UUID) : Registrable<UUID> {
    var attacking = ASConfig.defaultAttackMessageType
    var defensive = ASConfig.defaultDefendMessageType
    var regainHolo = ASConfig.defaultRegainHolo

    val default: Boolean
        get() = attacking == ASConfig.defaultAttackMessageType &&
                defensive == ASConfig.defaultDefendMessageType &&
                regainHolo == ASConfig.defaultRegainHolo


    fun default() {
        attacking = ASConfig.defaultAttackMessageType
        defensive = ASConfig.defaultDefendMessageType
        regainHolo = ASConfig.defaultRegainHolo
    }

    companion object {
        @JvmStatic
        fun fromStr(str: String, uuid: UUID): PersonalData? {
            val array = str.split(";")
            if (array.isEmpty() || array.size < 3) return null
            val personalData = PersonalData(uuid)
            personalData.attacking = array[0]
            personalData.defensive = array[1]
            personalData.regainHolo = array[2].toBoolean()
            return personalData
        }
    }

    override fun toString(): String {
        return "${attacking};${defensive};$regainHolo"
    }

    override fun register() {
        AttributeSystem.personalManager.register(this)
    }
}
