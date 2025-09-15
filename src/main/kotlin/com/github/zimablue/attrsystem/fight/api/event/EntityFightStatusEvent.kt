package com.github.zimablue.attrsystem.fight.api.event

import net.minestom.server.entity.LivingEntity
import net.minestom.server.event.trait.CancellableEvent

class EntityFightStatusEvent {

    /**
     * 实体进入战斗
     *
     * @property entity 实体
     */
    class In(val entity: LivingEntity) : CancellableEvent {
        private var isCancelled = false
        override fun isCancelled(): Boolean = isCancelled
        override fun setCancelled(cancel: Boolean) {
            isCancelled = cancel
        }
    }

    /**
     * 实体退出战斗
     *
     * @property entity 实体
     */
    class Out(val entity: LivingEntity) : CancellableEvent {
        private var isCancelled = false
        override fun isCancelled(): Boolean = isCancelled
        override fun setCancelled(cancel: Boolean) {
            isCancelled = cancel
        }
    }
}