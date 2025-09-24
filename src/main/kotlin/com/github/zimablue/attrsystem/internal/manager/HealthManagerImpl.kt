package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.api.AttrAPI
import com.github.zimablue.attrsystem.api.AttrAPI.getAttrData
import com.github.zimablue.attrsystem.api.attribute.Attribute
import com.github.zimablue.attrsystem.api.manager.HealthManager
import com.github.zimablue.attrsystem.internal.feature.database.ASContainer
import com.github.zimablue.attrsystem.internal.manager.ASConfig.debug
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.entity.EntityDamageEvent
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.trait.EntityEvent
import net.minestom.server.tag.Tag
import taboolib.common5.cdouble
import kotlin.math.min

object HealthManagerImpl : HealthManager() {
    private val HEALTH_TAG: Tag<Double> = Tag.Double("as_health")
    val enable: Boolean
        get() = ASConfig.options.getBoolean("health-scale.enable")
    val value: Int
        get() = ASConfig.options.getInt("health-scale.value",20)

    private val maxHealthAttr: Attribute by lazy { AttrAPI.attribute("CustomMaxHealth")?:error("CustomMaxHealth attribute not set") }

    // 血量缩放事件应当尽可能在最后处理，因为他会将伤害事件取消
    // 实际上RPG服务器的用户根本不必调用LivingEntity.damage()来处理伤害，只需要在自己的战斗系统中调用runFight计算伤害就可以了。
    private val damageEventNode: EventNode<EntityEvent> = EventNode.event("AttributeSystem-HealthManager-Damage", EventFilter.ENTITY) {
        it.entity is Player
    }.setPriority(999)
    private val dataEventNode: EventNode<Event> = EventNode.all("AttributeSystem-HealthManager-Data").setPriority(1)
    @Awake(PluginLifeCycle.ENABLE)
    fun onEnable() {
        AttributeSystem.asEventNode.addChild(damageEventNode)
        damageEventNode.addListener(EntityDamageEvent::class.java) { event ->
            val player = event.entity as Player
            if (!enable) return@addListener
            val currentHealth = getHealth(player)
            val newHealth = currentHealth - event.damage.amount
            setHealth(player, newHealth)
            event.isCancelled = true
            debug("Player ${player.username} took ${event.damage} damage, health set to $newHealth/${getMaxHealth(player)}")
        }
        AttributeSystem.asEventNode.addChild(damageEventNode)
        dataEventNode.addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
            val player = event.player
            val healthData = ASContainer[player.uuid, "as_health"]
            if(healthData==null) {
                setHealth(player, getMaxHealth(player))
            } else {
                setHealth(player, healthData.cdouble)
            }
        }
        dataEventNode.addListener(PlayerDisconnectEvent::class.java) { event ->
            val player = event.player
            val healthData = getHealth(player)
            ASContainer[player.uuid,"as_health"] = healthData.toString()
        }
    }

    override fun getMaxHealth(player: Player): Double {
        return player.getAttrData()?.getAttrValue<Double>(maxHealthAttr) ?: 0.0
    }

    override fun getHealth(player: Player): Double {
        return player.getTag(HEALTH_TAG)?:0.0
    }

    override fun setHealth(player: Player, health: Double) {
        val currentHealth = getHealth(player)
        if (currentHealth == health) return
        player.setTag(HEALTH_TAG, health)
        if(enable) {
            val maxHealth = getMaxHealth(player)
            // scaledHealth 可以为负值，这将杀死玩家
            val scaledHealth = (min(health,maxHealth) / maxHealth) * value
            player.health = scaledHealth.toFloat()
        } else {
            player.health = health.toFloat()
        }
    }
}