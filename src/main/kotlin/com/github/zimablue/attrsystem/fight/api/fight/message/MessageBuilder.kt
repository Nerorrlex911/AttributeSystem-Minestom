package com.github.zimablue.attrsystem.fight.api.fight.message

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.fight.api.fight.DamageType
import com.github.zimablue.attrsystem.fight.api.fight.FightData
import com.github.zimablue.devoutserver.util.map.component.Registrable

/**
 * @className MessageBuilder
 *
 * @author Glom
 * @date 2022/8/1 4:32 Copyright 2022 user. All rights reserved.
 */
interface MessageBuilder : Registrable<String> {
    fun build(
        damageType: DamageType,
        fightData: FightData,
        first: Boolean,
        type: Message.Type,
    ): Message

    override fun register() {
        AttributeSystem.messageBuilderManager.attack.register(this)
        AttributeSystem.messageBuilderManager.defend.register(this)
    }
}