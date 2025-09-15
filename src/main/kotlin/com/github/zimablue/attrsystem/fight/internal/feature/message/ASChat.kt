package com.github.zimablue.attrsystem.fight.internal.feature.message

import com.github.zimablue.attrsystem.fight.api.fight.FightData
import com.github.zimablue.attrsystem.fight.api.fight.message.Message
import com.github.zimablue.attrsystem.internal.manager.ASConfig
import com.github.zimablue.devoutserver.util.colored
import com.github.zimablue.pouplaceholder.PouPlaceholder
import net.minestom.server.entity.Player

class ASChat(val text: StringBuilder, override val fightData: FightData) : Message {

    override fun plus(message: Message, type: Message.Type): Message {
        message as ASChat
        text.append(separator(type)).append(message.text)
        return this
    }

    fun separator(type: Message.Type): String {
        return ASConfig.message.getString("fight-message.chat.${type.name.lowercase()}.separator") ?: "&5|"
    }

    override fun sendTo(vararg players: Player) {
        players.forEach { player -> player.sendMessage(PouPlaceholder.placeholderManager.replace(player,text.toString()).colored()) }
    }

}
