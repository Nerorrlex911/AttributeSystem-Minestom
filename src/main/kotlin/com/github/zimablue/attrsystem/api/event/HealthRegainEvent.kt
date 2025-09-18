package com.github.zimablue.attrsystem.api.event

import net.minestom.server.entity.LivingEntity
import net.minestom.server.event.trait.CancellableEvent

/**
 * 回血事件
 *
 * @constructor Create empty Health regain event
 * @property entity 实体
 * @property amount 回复量
 */
class HealthRegainEvent(val entity: LivingEntity, var amount: Double) : CancellableEvent {
    private var isCancelled = false
    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(p0: Boolean) {
        isCancelled = p0
    }
}