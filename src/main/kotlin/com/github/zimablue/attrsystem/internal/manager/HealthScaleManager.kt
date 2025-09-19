package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import net.minestom.server.entity.Player
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.event.player.PlayerPacketOutEvent
import net.minestom.server.network.packet.server.play.UpdateHealthPacket
import kotlin.math.round

object HealthScaleManager {
    val enable: Boolean
        get() = ASConfig.options.getBoolean("health-scale.enable")
    val value: Int
        get() = ASConfig.options.getInt("health-scale.value",20)

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
                event.isCancelled = true
                // 发送修改后的血量包，应当不会造成严重的延迟
                event.player.sendPacket(UpdateHealthPacket(scale(event.player), packet.food, packet.foodSaturation))
            }
        }
    }

}