package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.api.AttrAPI
import com.github.zimablue.attrsystem.api.AttrAPI.getAttrData
import com.github.zimablue.attrsystem.api.attribute.Attribute
import com.github.zimablue.attrsystem.api.event.AttributeUpdateEvent
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
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerRespawnEvent
import net.minestom.server.event.player.PlayerSpawnEvent
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
        AttributeSystem.asEventNode.addChild(dataEventNode)
        dataEventNode
            .addListener(PlayerSpawnEvent::class.java) { event ->
                scale(event.player)
            }
            .addListener(PlayerDisconnectEvent::class.java) { event ->
                val player = event.player
                val healthData = getHealth(player)
                ASContainer[player.uuid,"as_health"] = healthData.toString()
            }
            .addListener(PlayerRespawnEvent::class.java) { event ->
                val player = event.player
                setHealth(player, getMaxHealth(player))
            }
            .addListener(AttributeUpdateEvent.Post::class.java) { event ->
                val player = event.entity as? Player?:return@addListener
                val maxHealth = getMaxHealth(player)
                scale(player,maxHealth=maxHealth)
            }
    }

    override fun getMaxHealth(player: Player): Double {
        return player.getAttrData()?.getAttrValue<Double>(maxHealthAttr) ?: 0.0
    }

    override fun getHealth(player: Player): Double {
        val health = player.getTag(HEALTH_TAG)
        if(health==null) {
            val newHealth = ASContainer[player.uuid, "as_health"]?.cdouble ?: getMaxHealth(player)
            player.setTag(HEALTH_TAG, newHealth)
            scale(player, newHealth)
            return newHealth
        }
        return health
    }

    override fun setHealth(player: Player, health: Double) {
        val currentHealth = getHealth(player)
        if (currentHealth == health) return
        // 可以为负值，这将杀死玩家
        if (health<=0) {
            player.setTag(HEALTH_TAG, 0.0)
            player.health = 0f
            return
        }
        player.setTag(HEALTH_TAG, health)
        debug("setHealth $currentHealth, health set to $health")
        scale(player,health)
    }
    private fun scale(player: Player, health: Double=getHealth(player), maxHealth: Double=getMaxHealth(player)) {
        if(enable) {
            // scaledHealth
            val scaledHealth = ((min(health,maxHealth) / maxHealth) * value).toFloat()
            if(scaledHealth!=player.health) player.health = scaledHealth
        }
    }
}