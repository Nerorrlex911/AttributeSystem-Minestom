package com.github.zimablue.attrsystem.api.event

import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.event.trait.CancellableEvent

/**
 * 原版属性更新后
 *
 * @property entity 实体
 * @property attr 属性
 * @property value 属性值
 */
class VanillaAttributeUpdateEvent (
    val entity: LivingEntity,
    val attr: Attribute,
    val value: Double,
) : CancellableEvent {
    private var isCancelled = false
    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(p0: Boolean) {
        isCancelled = p0
    }

}
