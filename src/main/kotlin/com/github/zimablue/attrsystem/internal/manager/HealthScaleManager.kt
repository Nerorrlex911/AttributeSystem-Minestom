package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.internal.manager.ASConfig.debug
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.event.player.PlayerPacketOutEvent
import net.minestom.server.network.packet.server.play.EntityAttributesPacket
import net.minestom.server.network.packet.server.play.UpdateHealthPacket
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.round

//object HealthScaleManager {
//    val enable: Boolean
//        get() = ASConfig.options.getBoolean("health-scale.enable")
//    val value: Int
//        get() = ASConfig.options.getInt("health-scale.value",20)
//    // 线程安全的一次性标记集合
//    private val ignoreSet = ConcurrentHashMap.newKeySet<UUID>()
//    private val ignoreSet2 = ConcurrentHashMap.newKeySet<UUID>()
//
//    private fun scale(player: Player) : Float {
//        val percent = player.health / player.getAttributeValue(Attribute.MAX_HEALTH)
//        return round(value * percent).toFloat()
//    }
//
//    @Awake(PluginLifeCycle.ENABLE)
//    fun onEnable() {
//        // 事件监听不应当支持热重载
//        if (enable) {
//            AttributeSystem.asEventNode.addListener(PlayerPacketOutEvent::class.java) { event ->
//                val packet = event.packet
//                if (packet is UpdateHealthPacket) {
//                    val player = event.player
//                    // 如果是我们自己发的包，直接跳过，并清理标记
//                    if (ignoreSet.remove(player.uuid)) {
//                        return@addListener
//                    }
//                    event.isCancelled = true
//                    // 标记：接下来这个玩家的下一个包忽略处理
//                    ignoreSet.add(player.uuid)
//                    // 发送修改后的血量包，应当不会造成严重的延迟
//                    debug("[HealthScale] Send UpdateHealthPacket to ${player.username}: ${scale(player)} (original: ${packet.health})")
//                    player.sendPacket(UpdateHealthPacket(scale(player), packet.food, packet.foodSaturation))
//                } else if(packet is EntityAttributesPacket) {
//                    val player = findPlayerByID(packet.entityId)?:return@addListener
//                    if(ignoreSet2.remove(player.uuid)) {
//                        return@addListener
//                    }
//                    val indexes = mutableListOf<Int>()
//                    packet.properties.forEachIndexed { i,property ->
//                        if (property.attribute == Attribute.MAX_HEALTH) {
//                            indexes.add(i)
//                        }
//                    }
//                    if (indexes.isNotEmpty()) {
//                        val newProperties = packet.properties.toMutableList()
//                        indexes.forEach { index ->
//                            val property = newProperties[index]
//                            val newProperty = EntityAttributesPacket.Property(
//                                property.attribute,
//                                value.toDouble(),
//                                property.modifiers
//                            )
//                            newProperties[index] = newProperty
//                        }
//                        val newPacket = EntityAttributesPacket(packet.entityId, newProperties)
//                        event.isCancelled = true
//                        // 标记：接下来这个玩家的下一个包忽略处理
//                        ignoreSet2.add(player.uuid)
//                        debug("[HealthScale] EntityAttributesPacket to ${player.username}: $value")
//                        player.sendPacket(newPacket)
//                    }
//                }
//            }
//        }
//    }
//    private fun findPlayerByID(id: Int): Player? {
//        // 理论上玩家数量应当远小于实体数量，所以遍历玩家以匹配
//        MinecraftServer.getConnectionManager().onlinePlayers.forEach {player ->
//            if(player.entityId == id) {
//                return player
//            }
//        }
//        return null
//    }
//
//}