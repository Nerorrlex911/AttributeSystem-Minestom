package com.github.zimablue.attrsystem.fight.manager

import com.github.zimablue.attrsystem.fight.api.fight.message.MessageBuilder
import com.github.zimablue.attrsystem.fight.api.manager.MessageBuilderManager
import com.github.zimablue.attrsystem.fight.internal.feature.message.builder.ASActionBarBuilder
import com.github.zimablue.attrsystem.fight.internal.feature.message.builder.ASChatBuilder
import com.github.zimablue.attrsystem.fight.internal.feature.message.builder.ASHoloBuilder
import com.github.zimablue.attrsystem.fight.internal.feature.message.builder.ASTitleBuilder
import com.github.zimablue.devoutserver.util.map.LowerKeyMap

object MessageBuilderManagerImpl:MessageBuilderManager() {

    init {
        ASActionBarBuilder.register()
        ASChatBuilder.register()
        ASHoloBuilder.register()
        ASTitleBuilder.register()
    }

    override val attack = LowerKeyMap<MessageBuilder>()
    override val defend = LowerKeyMap<MessageBuilder>()
}