package com.github.zimablue.attrsystem.fight.internal.feature.message.builder

import com.github.zimablue.attrsystem.fight.api.fight.DamageType
import com.github.zimablue.attrsystem.fight.api.fight.FightData
import com.github.zimablue.attrsystem.fight.api.fight.message.Message
import com.github.zimablue.attrsystem.fight.api.fight.message.MessageBuilder
import com.github.zimablue.attrsystem.internal.manager.ASConfig.message
import com.github.zimablue.attrsystem.fight.internal.feature.message.ASTitle

object ASTitleBuilder : MessageBuilder {

    override val key: String = "title"


    override fun build(
        damageType: DamageType,
        fightData: FightData,
        first: Boolean,
        type: Message.Type,
    ): Message {
        val typeStr = type.name.lowercase()
        val title = fightData.handleStr(damageType["$typeStr-title"].toString().replace("{name}", damageType.name))
        val subTitle =
            fightData.handleStr(damageType["$typeStr-sub-title"].toString().replace("{name}", damageType.name))
        val titleStr =
            if (first) fightData.handleStr(
                message.getString("fight-message.title.$typeStr.title")?.replace("{message}", title)
                    ?: title
            )
            else title

        val subTitleStr =
            if (first) fightData.handleStr(
                message.getString("fight-message.title.$typeStr.sub-title")
                    ?.replace("{message}", subTitle)
                    ?: subTitle
            )
            else subTitle

        return ASTitle(StringBuilder(titleStr), StringBuilder(subTitleStr), fightData)
    }
}
