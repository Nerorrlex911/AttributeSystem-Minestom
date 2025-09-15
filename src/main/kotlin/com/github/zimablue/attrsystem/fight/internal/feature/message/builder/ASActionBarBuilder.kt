package com.github.zimablue.attrsystem.fight.internal.feature.message.builder

import com.github.zimablue.attrsystem.fight.api.fight.DamageType
import com.github.zimablue.attrsystem.fight.api.fight.FightData
import com.github.zimablue.attrsystem.fight.api.fight.message.Message
import com.github.zimablue.attrsystem.fight.api.fight.message.MessageBuilder
import com.github.zimablue.attrsystem.internal.manager.ASConfig

import com.github.zimablue.attrsystem.fight.internal.feature.message.ASActionBar

object ASActionBarBuilder : MessageBuilder {

    override val key: String = "action_bar"

    override fun build(
        damageType: DamageType,
        fightData: FightData,
        first: Boolean,
        type: Message.Type,
    ): Message {
        val typeStr = type.name.lowercase()
        val typeText =
            fightData.handleStr(damageType["$typeStr-action-bar"].toString().replace("{name}", damageType.name))
        val text =
            if (first) fightData.handleStr(
                ASConfig.message.getString("fight-message.action-bar.$typeStr.text")
                    ?.replace("{message}", typeText)
                    ?: typeText
            )
            else typeText

        return ASActionBar(StringBuilder(text), fightData)
    }
}
