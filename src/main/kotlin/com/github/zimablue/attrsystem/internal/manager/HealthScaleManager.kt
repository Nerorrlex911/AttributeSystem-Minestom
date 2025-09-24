package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.internal.manager.ASConfig.debug
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.event.entity.EntityDamageEvent
import net.minestom.server.event.player.PlayerPacketOutEvent
import net.minestom.server.network.packet.server.play.EntityAttributesPacket
import net.minestom.server.network.packet.server.play.UpdateHealthPacket
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.round

object HealthScaleManager {
    val enable: Boolean
        get() = ASConfig.options.getBoolean("health-scale.enable")
    val value: Int
        get() = ASConfig.options.getInt("health-scale.value",20)
    // 线程安全的一次性标记集合
    private val ignoreSet = ConcurrentHashMap.newKeySet<UUID>()
    private val ignoreSet2 = ConcurrentHashMap.newKeySet<UUID>()

    private fun scale(player: Player) : Float {
        val percent = player.health / player.getAttributeValue(Attribute.MAX_HEALTH)
        return round(value * percent).toFloat()
    }

    fun onEnable() {
    }
    private fun findPlayerByID(id: Int): Player? {
        // 理论上玩家数量应当远小于实体数量，所以遍历玩家以匹配
        MinecraftServer.getConnectionManager().onlinePlayers.forEach {player ->
            if(player.entityId == id) {
                return player
            }
        }
        return null
    }

}