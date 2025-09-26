package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.api.AttrAPI.addCompiledData
import com.github.zimablue.attrsystem.api.AttrAPI.removeCompiledData
import com.github.zimablue.attrsystem.api.event.PotionAddEvent
import com.github.zimablue.attrsystem.api.event.PotionRemoveEvent
import com.github.zimablue.attrsystem.api.manager.PotionManager
import com.github.zimablue.attrsystem.api.potion.PotionData
import com.github.zimablue.attrsystem.api.potion.PotionDataCompound
import com.github.zimablue.attrsystem.internal.feature.database.ASContainer
import com.github.zimablue.attrsystem.internal.manager.ASConfig.debug
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.AwakePriority
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventDispatcher
import net.minestom.server.event.EventNode
import net.minestom.server.event.entity.EntityDeathEvent
import net.minestom.server.event.entity.EntityDespawnEvent
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.utils.time.TimeUnit
import java.time.Duration

object PotionManagerImpl : PotionManager(){

    private val potionEventNode: EventNode<Event> = EventNode.all("PotionManager").setPriority(4)

    override fun addPotion(
        entity: LivingEntity,
        data: Map<String,Any>,
        source: String,
        duration: Long,
        persistent: Boolean,
        removeOnDeath: Boolean,
    ) : Boolean{
        debug("PotionData: $data")
        val potionData = PotionData(data, duration, persistent, removeOnDeath)
        return addPotion(entity,source,potionData)
    }
    override fun addPotion(entity: LivingEntity, source: String, potionData: PotionData) : Boolean {
        val uuid = entity.uuid
        val event = PotionAddEvent(entity, source, potionData)
        EventDispatcher.call(event)
        if(event.isCancelled) return false
        val newPotionData = event.potionData
        val entityData = this.computeIfAbsent(uuid){ PotionDataCompound(uuid) }
        // 如果已经有此源的药水，应当先删除
        if(entityData.containsKey(source)) {
            removePotion(entity,source)
        }
        AttributeSystem.readManager.readMap(newPotionData.data,entity)?.let {
            debug("PotionData: ${it.serialize()}")
            entity.addCompiledData(source, it)
            val task = entity.scheduler()
                .buildTask { entity.removeCompiledData(source) }
                .delay(Duration.of(newPotionData.duration,TimeUnit.SERVER_TICK))
                .schedule()
            newPotionData.task = task
            entityData[source] = newPotionData
        }
        return true
    }
    override fun removePotion(entity: LivingEntity, source: String) {
        val uuid = entity.uuid
        val entityData = this[uuid]?:return
        // call PotionRemoveEvent
        val potionData = entityData[source]
        val event = PotionRemoveEvent(entity, source, potionData)
        if(event.isCancelled) return
        // 删除药水的同时，取消任务
        entityData.remove(source)?.release()
        entity.removeCompiledData(source)
    }

    @Awake(PluginLifeCycle.ENABLE,AwakePriority.HIGH)
    fun onEnable() {
        AttributeSystem.asEventNode.addChild(potionEventNode)
            potionEventNode.addListener(EntityDespawnEvent::class.java) { event ->
                val entity = event.entity
                remove(entity.uuid)
            }
            .addListener(EntityDeathEvent::class.java) { event ->
                val entity = event.entity
                if(entity !is Player) {
                    remove(entity.uuid)
                    return@addListener
                }
                // 如果是玩家，将死亡后移除的PotionData移除
                val compound = this[entity.uuid]?:return@addListener
                compound.filter { it.value.removeOnDeath }.forEach {
                    removePotion(entity,it.key)
                }
            }
            .addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
                val uuid = event.entity.uuid
                ASContainer[uuid, "potion_data"]?.let {
                    val compound = PotionDataCompound.deserialize(uuid, it)
                    compound.forEach { source, potionData ->
                        addPotion(event.entity, source, potionData)
                    }
                    register(compound)
                }
            }
            .addListener(PlayerDisconnectEvent::class.java) { event ->
                val uuid = event.entity.uuid
                ASContainer[uuid, "potion_data"] = get(uuid)?.serialize()
                remove(uuid)
            }

    }

}