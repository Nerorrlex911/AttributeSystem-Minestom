package com.github.zimablue.attrsystem.fight.internal.feature.message.builder

import com.github.zimablue.attrsystem.fight.api.fight.DamageType
import com.github.zimablue.attrsystem.fight.api.fight.FightData
import com.github.zimablue.attrsystem.fight.api.fight.message.Message
import com.github.zimablue.attrsystem.fight.api.fight.message.MessageBuilder
import com.github.zimablue.attrsystem.fight.internal.feature.message.ASHologramGroup

object ASHoloBuilder : MessageBuilder {

    override val key: String = "holo"


    override fun build(
        damageType: DamageType,
        fightData: FightData,
        first: Boolean,
        type: Message.Type,
    ): Message {
        val typeStr = type.name.lowercase()
        val text = fightData.handleStr(damageType["$typeStr-holo"].toString(), false).replace("{name}", damageType.name)
        return ASHologramGroup(
            mutableListOf(text),
            fightData.defender!!.run{ position.withY { it+eyeHeight } },
            fightData.defender!!.instance,
            "fight-message.holo",
            fightData
        )
    }
}
