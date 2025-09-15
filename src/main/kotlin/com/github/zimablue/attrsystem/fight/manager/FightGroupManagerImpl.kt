package com.github.zimablue.attrsystem.fight.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.fight.api.FightAPI
import com.github.zimablue.attrsystem.fight.api.event.FightEvent
import com.github.zimablue.attrsystem.fight.api.fight.FightData
import com.github.zimablue.attrsystem.fight.api.fight.message.MessageData
import com.github.zimablue.attrsystem.fight.api.manager.FightGroupManager
import com.github.zimablue.attrsystem.fight.internal.core.fight.FightGroup
import com.github.zimablue.attrsystem.utils.getAllFiles
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import net.minestom.server.entity.Player
import net.minestom.server.entity.damage.Damage
import net.minestom.server.tag.Tag
import taboolib.common5.cfloat
import taboolib.module.configuration.Configuration

object FightGroupManagerImpl : FightGroupManager() {

    @Awake(PluginLifeCycle.ENABLE)
    fun onEnable() {
        onReload()
        addIgnore()
    }

    @Awake(PluginLifeCycle.RELOAD)
    fun onReload() {
        clear()
        getAllFiles(AttributeSystem.dataDirectory.resolve("fight_group").toFile()).forEach {
            val conf = Configuration.loadFromFile(it)
            conf.getKeys(false).map { key ->
                val section = conf.getConfigurationSection(key) ?: return@map
                FightGroup.deserialize(section)?.register()
            }
        }
    }

    private fun addIgnore() {
        FightAPI.addIgnoreAttack { _, defender ->
            defender.getTag(Tag.Boolean("doing-skill-damage")) ?: false
        }

    }

    private fun FightData.doingDamage(result: Double): Double {
        val tag = Tag.Boolean("skill-damage")
        defender?.setTag(tag, true)
        defender?.damage(Damage.fromEntity(attacker,result.cfloat))
        defender?.removeTag(tag)
        return result
    }

    override fun runFight(key: String, data: FightData, message: Boolean, damage: Boolean): Double {

        if (!AttributeSystem.fightGroupManager.containsKey(key)) return -1.0

        val fightData = data.apply {
            putIfAbsent("projectile", false)
            putIfAbsent("origin", 0.0)
            putIfAbsent("force", 1.0)
            putIfAbsent("charge", 1.0)
            this["type"] = when {
                attacker is Player && defender is Player -> "PVP"
                attacker is Player && defender !is Player -> "PVE"
                else -> "EVE"
            }
            calMessage = message
        }

        val messageData = MessageData()

        val before = FightEvent.Pre(key, fightData)
        AttributeSystem.asEventNode.call(before)
        var eventFightData = before.fightData
        //println("Pre Cancelled : ${before.isCancelled}")
        if (before.isCancelled || eventFightData.event?.isCancelled == true) return -0.1
        val result = AttributeSystem.fightGroupManager[key]!!.run(eventFightData)

        val process = FightEvent.Process(key, eventFightData)
        AttributeSystem.asEventNode.call(process)
        if (process.isCancelled || eventFightData.event?.isCancelled == true) return -0.1

        eventFightData = process.fightData
        val post = FightEvent.Post(key, eventFightData)
        AttributeSystem.asEventNode.call(post)
        eventFightData = post.fightData
        if (post.isCancelled || eventFightData.event?.isCancelled == true) return -0.1

        if (message) {
            eventFightData.calMessage()
            messageData.addAll(eventFightData.messageData)
            messageData.send(fightData.attacker as? Player?, fightData.defender as? Player?)
        }

        val damageValue = result.apply(eventFightData)
        return if (damage) data.doingDamage(damageValue)
        else damageValue
    }
}
