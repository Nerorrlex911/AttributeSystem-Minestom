package com.github.zimablue.attrsystem.fight.internal.feature.message

import com.github.zimablue.attrsystem.fight.api.fight.FightData
import com.github.zimablue.attrsystem.fight.api.fight.message.Message
import com.github.zimablue.attrsystem.internal.manager.ASConfig
import com.github.zimablue.attrsystem.utils.toMiniMessage
import com.github.zimablue.devoutserver.util.colored
import com.github.zimablue.pouplaceholder.PouPlaceholder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.entity.Player
import net.minestom.server.utils.time.TimeUnit

class ASActionBar(
    private val text: StringBuilder, override val fightData: FightData,
) : Message {


    fun separator(type: Message.Type): String {
        return ASConfig.message.getString("fight-message.action-bar.${type.name.lowercase()}.separator") ?: "&5|"
    }

    private fun append(text: StringBuilder, type: Message.Type): ASActionBar {
        this.text.append(separator(type)).append(text)
        return this
    }


    override fun sendTo(vararg players: Player) {
        players.forEach { player ->
            sendActionBar(
                player,
                PouPlaceholder.placeholderManager.replace(player,text.toString()).colored(),
                ASConfig.message.getLong("fight-message.action-bar.stay"),
            )
        }
    }

    override fun plus(message: Message, type: Message.Type): Message {
        message as ASActionBar
        return this.append(message.text, type)
    }

    fun sendActionBar(player: Player, text: String, stay: Long) {
        player.sendActionBar(text.toMiniMessage())
        player.scheduler().buildTask { (player.sendActionBar(Component.text(""))) }
            .delay(stay,TimeUnit.SERVER_TICK)
            .schedule()
    }

}
