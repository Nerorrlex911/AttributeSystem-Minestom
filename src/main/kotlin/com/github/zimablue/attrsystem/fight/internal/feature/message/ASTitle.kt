package com.github.zimablue.attrsystem.fight.internal.feature.message

import com.github.zimablue.attrsystem.fight.api.fight.FightData
import com.github.zimablue.attrsystem.fight.api.fight.message.Message
import com.github.zimablue.attrsystem.internal.manager.ASConfig
import com.github.zimablue.attrsystem.utils.placeholder
import com.github.zimablue.attrsystem.utils.toMiniMessage
import com.github.zimablue.devoutserver.util.colored
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.minestom.server.entity.Player

class ASTitle(
    val title: StringBuilder,
    val subTitle: StringBuilder, override val fightData: FightData,
) : Message {

    fun separator(type: Message.Type): String {
        return ASConfig.message.getString("fight-message.title.${type.name.lowercase()}.separator") ?: "&5|"
    }

    private fun appendTitle(title: StringBuilder, type: Message.Type): ASTitle {
        if (!this.title.toString().contains("null") && !title.toString().contains("null"))
            this.title.append(separator(type)).append(title)
        return this
    }

    private fun appendSubtitle(subTitle: StringBuilder, type: Message.Type): ASTitle {
        if (!this.subTitle.toString().contains("null") && !subTitle.toString().contains("null"))
            this.subTitle.append(separator(type)).append(subTitle)
        return this
    }

    override fun plus(message: Message, type: Message.Type): ASTitle {
        message as ASTitle
        return this.appendTitle(message.title, type).appendSubtitle(message.subTitle, type)
    }

    override fun sendTo(vararg players: Player) {
        val section = ASConfig.message.getConfigurationSection("fight-message.title")
        players.forEach { player ->
            val titleStr = this.title.toString().placeholder(player)
            val subTitleStr = this.subTitle.toString().placeholder(player)
            val title: String? = if (titleStr != "null") titleStr else null
            val subTitle: String? = if (subTitleStr != "null") subTitleStr else null
            player.sendTitle(
                title ?: "",
                subTitle ?: "",
                section?.getInt("fade-in") ?: 0,
                section?.getInt("stay") ?: 20,
                section?.getInt("fade-out") ?: 0
            )
        }
    }
    fun Player.sendTitle(title: String, subTitle: String, fadeIn: Int, stay: Int, fadeOut: Int) {
        showTitle(Title.title(
            if(title.isEmpty()) Component.empty() else title.toMiniMessage(),
            if(subTitle.isEmpty()) Component.empty() else subTitle.toMiniMessage(),
            fadeIn,
            stay,
            fadeOut
        ))
    }
}
