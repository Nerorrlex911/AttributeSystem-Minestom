package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.AttributeSystem.attributeDataManager
import com.github.zimablue.attrsystem.AttributeSystem.attributeSystemAPI
import com.github.zimablue.attrsystem.internal.core.schedule.TaskScheduler
import com.github.zimablue.attrsystem.internal.manager.AttributeSystemAPIImpl.remove
import com.github.zimablue.attrsystem.utils.isAlive
import com.github.zimablue.attrsystem.utils.validEntity
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.AwakePriority
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.event.entity.EntityDeathEvent
import net.minestom.server.event.entity.EntityDespawnEvent
import net.minestom.server.event.entity.EntitySpawnEvent
import net.minestom.server.event.entity.EntityTickEvent
import net.minestom.server.event.inventory.InventoryItemChangeEvent
import net.minestom.server.event.player.*
import net.minestom.server.inventory.PlayerInventory
import taboolib.common5.Baffle
import taboolib.common5.clong
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
            50 * updatePeriod.clong
        ) {
            // 扫描所有已缓存的实体，移除无效实体的数据(一般情况下不会出现无效数据，但以防万一)
            // scanning all cached entities and removing invalid entity data (generally invalid data will not appear, but just in case)
            for (uuid in attributeDataManager.keys) {
                val entity = uuid.validEntity()
                if (entity == null || !entity.isActive || entity.isDead) {
                    if (entity !is Player)
                        remove(uuid)
                    continue
                }
            }
        }
        with(AttributeSystem.asEventNode) {
            addListener(EntityTickEvent::class.java) { event ->
                val entity = event.entity
                if(!entity.isAlive()) return@addListener
                entity as LivingEntity
                if(entity.aliveTicks%updatePeriod == 0L) attributeSystemAPI.update(entity)
                HealthRegainManager.regain(entity)
            }
            addListener(EntitySpawnEvent::class.java) { event ->
                if(event.entity.isAlive()) attributeSystemAPI.update(event.entity as LivingEntity)
            }
            addListener(EntityDespawnEvent::class.java) { event ->
                attributeSystemAPI.remove(event.entity.uuid)
            }
            addListener(EntityDeathEvent::class.java) { event ->
                if(event.entity !is Player) attributeSystemAPI.remove(event.entity.uuid)
            }
            addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
                event.player.updateNextTick()
            }
            addListener(PlayerRespawnEvent::class.java) {event ->
                event.player.updateNextTick()
            }
            addListener(PlayerSpawnEvent::class.java) { event ->
                event.player.updateNextTick()
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
                        player.updateNextTick()
                    }
                }
            }
        }
    }

    internal fun LivingEntity.updateNextTick() {
        if (baffle.hasNext(uuid.toString())) {
            this.scheduleNextTick { attributeSystemAPI.update(it as LivingEntity) }
        }
    }
    @Awake(PluginLifeCycle.RELOAD,AwakePriority.HIGH)
    fun onReload() {
        baffle.resetAll()
        baffle = Baffle.of(updateBaffle, TimeUnit.MILLISECONDS)
    }
}