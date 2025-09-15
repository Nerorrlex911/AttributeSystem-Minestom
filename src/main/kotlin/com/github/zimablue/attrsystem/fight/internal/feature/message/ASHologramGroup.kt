package com.github.zimablue.attrsystem.fight.internal.feature.message

import com.github.zimablue.attrsystem.fight.api.fight.FightData
import com.github.zimablue.attrsystem.fight.api.fight.message.Message
import com.github.zimablue.attrsystem.fight.internal.feature.hologram.HologramBuilder
import com.github.zimablue.attrsystem.internal.manager.ASConfig
import com.github.zimablue.attrsystem.utils.toMiniMessage
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import taboolib.common5.Coerce

/**
 * A s hologram group
 *
 * @constructor Create empty A s hologram group
 * @property texts
 * @property pos
 * @property node
 */
class ASHologramGroup(
    val texts: MutableList<String>,
    private val pos: Pos,
    private val instance: Instance,
    val node: String,
    override val fightData: FightData,
) : Message {

    private fun Any?.toDouble(): Double {
        return Coerce.toDouble(this)
    }

    override fun sendTo(vararg players: Player) {
        val map = fightData.handleMap(ASConfig.message.getConfigurationSection(node)!!.toMap(), false)
        val begin = map["begin"] as Map<String, Any>
        val beginPos = Pos(begin["x"].toDouble(), begin["y"].toDouble(), begin["z"].toDouble())
        val end = map["end"] as Map<String, Any>
        val endPos = Pos(end["x"].toDouble(), end["y"].toDouble(), end["z"].toDouble())
        val stay = map["stay"].toDouble().toLong()
        val time = map["time"].toDouble().toInt()
        HologramBuilder(instance,pos.add(beginPos))
            .content(texts.map{it.toMiniMessage()})
            .stay(stay)
            .animation(time, pos.add(endPos))
            .viewers(*players)
            .build()
    }

    override fun plus(message: Message, type: Message.Type): Message {
        message as ASHologramGroup
        this.texts.addAll(message.texts)
        return this
    }
}
