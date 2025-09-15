package com.github.zimablue.attrsystem.fight.internal.feature.message.builder

import com.github.zimablue.attrsystem.fight.api.fight.DamageType
import com.github.zimablue.attrsystem.fight.api.fight.FightData
import com.github.zimablue.attrsystem.fight.api.fight.message.Message
import com.github.zimablue.attrsystem.fight.api.fight.message.MessageBuilder
import com.github.zimablue.attrsystem.internal.manager.ASConfig.message
import com.github.zimablue.attrsystem.fight.internal.feature.message.ASChat

object ASChatBuilder : MessageBuilder {

    override val key: String = "chat"


    override fun build(
        damageType: DamageType,
        fightData: FightData,
        first: Boolean,
        type: Message.Type,
    ): Message {
        val typeStr = type.name.lowercase()
        val typeText = fightData.handleStr(damageType["$typeStr-chat"].toString().replace("{name}", damageType.name))
        val text = if (first) fightData.handleStr(
            message.getString("fight-message.chat.$typeStr.text")
                ?.replace("{message}", typeText) ?: typeText
        )
        else typeText
        return ASChat(StringBuilder(text), fightData)
    }
}
