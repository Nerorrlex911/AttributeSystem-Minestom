package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import net.minestom.server.entity.Player
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.event.player.PlayerPacketOutEvent
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

    private fun scale(player: Player) : Float {
        val percent = player.health / player.getAttributeValue(Attribute.MAX_HEALTH)
        return round(value * percent).toFloat()
    }

    @Awake(PluginLifeCycle.ENABLE)
    fun onEnable() {
        // 事件监听不应当支持热重载
        if (enable) {
            AttributeSystem.asEventNode.addListener(PlayerPacketOutEvent::class.java) { event ->
                val packet = event.packet
                if (packet !is UpdateHealthPacket) return@addListener
                val player = event.player
                // 如果是我们自己发的包，直接跳过，并清理标记
                if (ignoreSet.remove(player.uuid)) {
                    return@addListener
                }
                event.isCancelled = true
                // 标记：接下来这个玩家的下一个包忽略处理
                ignoreSet.add(player.uuid)
                // 发送修改后的血量包，应当不会造成严重的延迟
                player.sendPacket(UpdateHealthPacket(scale(player), packet.food, packet.foodSaturation))
            }
        }
    }

}