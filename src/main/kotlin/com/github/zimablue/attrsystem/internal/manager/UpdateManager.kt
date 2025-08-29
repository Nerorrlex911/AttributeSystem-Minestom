package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.AttributeSystem.attributeDataManager
import com.github.zimablue.attrsystem.AttributeSystem.attributeSystemAPI
import com.github.zimablue.attrsystem.api.AttrAPI.update
import com.github.zimablue.attrsystem.internal.core.schedule.TaskScheduler
import com.github.zimablue.attrsystem.internal.manager.AttributeSystemAPIImpl.remove
import com.github.zimablue.attrsystem.utils.validEntity
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.AwakePriority
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.event.entity.EntityDeathEvent
import net.minestom.server.event.entity.EntityDespawnEvent
import net.minestom.server.event.inventory.InventoryItemChangeEvent
import net.minestom.server.event.player.*
import net.minestom.server.inventory.PlayerInventory
import net.minestom.server.timer.TaskSchedule
import taboolib.common5.Baffle
import java.util.concurrent.TimeUnit

object UpdateManager {

    val updatePeriod: Int
        get() = ASConfig.config.getInt("update.period",10)
    val updateBaffle: Long
        get() = ASConfig.config.getLong("update.baffle",20)

    private var baffle = Baffle.of(20,TimeUnit.MILLISECONDS)

    @Awake(PluginLifeCycle.ENABLE,AwakePriority.HIGH)
    fun onEnable() {
        TaskScheduler.schedule(
            "UpdateManager",
            500
        ) {
            for (uuid in attributeDataManager.keys) {
                val entity = uuid.validEntity()
                if (entity == null || !entity.isActive || entity.isDead) {
                    if (entity !is Player)
                        remove(uuid)
                    continue
                }
                attributeSystemAPI.update(entity)
            }
        }
        with(AttributeSystem.asEventNode) {
            addListener(EntityDespawnEvent::class.java) { event ->
                attributeSystemAPI.remove(event.entity.uuid)
            }
            addListener(EntityDeathEvent::class.java) { event ->
                if(event.entity !is Player) attributeSystemAPI.remove(event.entity.uuid)
            }
            addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
                attributeSystemAPI.update(event.player)
            }
            addListener(PlayerRespawnEvent::class.java) {event ->
                event.player.updateSync(1)
            }
            addListener(PlayerSpawnEvent::class.java) { event ->
                event.player.updateSync(1)
            }
            addListener(PlayerDisconnectEvent::class.java) { event ->
                attributeSystemAPI.remove(event.player.uuid)
                baffle.reset(event.player.uuid.toString())
            }
            //感谢伟大的Minestom直接提供了背包物品更改事件，我们不需要监听各种拾取点击丢弃事件了
            addListener(InventoryItemChangeEvent::class.java) { event ->
                val inventory = event.inventory
                if(inventory is PlayerInventory) {
                    for (player in inventory.viewers) {
                        player.updateSync(1)
                    }
                }
            }
        }
    }

    internal fun LivingEntity.updateSync(delay: Int = 0) {
        if (baffle.hasNext(uuid.toString())) {
            MinecraftServer.getSchedulerManager().buildTask {
                attributeSystemAPI.update(this)
            }.delay(TaskSchedule.tick(delay)).schedule()
        }
    }

    fun onReload() {
        baffle.resetAll()
        baffle = Baffle.of(updateBaffle, TimeUnit.MILLISECONDS)
    }
}