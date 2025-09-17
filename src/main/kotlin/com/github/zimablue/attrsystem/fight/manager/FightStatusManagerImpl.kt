package com.github.zimablue.attrsystem.fight.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.fight.api.event.EntityFightStatusEvent
import com.github.zimablue.attrsystem.fight.api.manager.FightStatusManager
import com.github.zimablue.attrsystem.internal.manager.ASConfig
import com.github.zimablue.attrsystem.utils.livingEntity
import com.github.zimablue.attrsystem.utils.sendLang
import com.github.zimablue.devoutserver.util.map.BaseMap
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.event.EventDispatcher
import net.minestom.server.timer.Task
import net.minestom.server.utils.time.TimeUnit
import java.util.*

object FightStatusManagerImpl : FightStatusManager() {

    private val fights: MutableCollection<UUID> = Collections.synchronizedCollection(HashSet<UUID>())
    private val tasks = BaseMap<UUID, Task>()

    override fun isFighting(uuid: UUID): Boolean {
        return fights.contains(uuid)
    }

    override fun isFighting(entity: LivingEntity): Boolean {
        return isFighting(entity.uuid)
    }

    override fun intoFighting(uuid: UUID) {
        uuid.livingEntity()?.let { intoFighting(it) }
    }


    override fun outFighting(uuid: UUID) {
        uuid.livingEntity()?.let { outFighting(it) }
    }

    val isEnable: Boolean
        get() = ASConfig.options.getBoolean("fight-status.enable",true)
    val exitTime: Long
        get() = ASConfig.options.getLong("fight-status.exit-time",100)

    override fun intoFighting(entity: LivingEntity) {
        if (!isEnable) return
        val uuid = entity.uuid
        val event = EntityFightStatusEvent.In(entity as? Player? ?: return)
        EventDispatcher.call(event)
        if (event.isCancelled) return
        if (!fights.contains(uuid)) {
            (entity as? Player?)?.sendLang("fight-in")
        }
        tasks[uuid]?.cancel()
        tasks.remove(uuid)
        fights.add(uuid)
        tasks[uuid] = entity.scheduler().buildTask{
            outFighting(entity)
        }.delay(exitTime,TimeUnit.SERVER_TICK).schedule()
    }

    override fun outFighting(entity: LivingEntity) {
        val uuid = entity.uuid
        val event = EntityFightStatusEvent.Out(entity)
        EventDispatcher.call(event)
        if (event.isCancelled) return
        fights.remove(uuid)
        tasks[uuid]?.cancel()
        tasks.remove(uuid)
        (entity as? Player?)?.sendLang("fight-out")
    }

}
