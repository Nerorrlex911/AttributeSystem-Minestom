package com.github.zimablue.attrsystem.api.event

import net.minestom.server.entity.Entity
import net.minestom.server.event.trait.CancellableEvent
import net.minestom.server.item.ItemStack

/**
 * 加载物品事件
 *
 * 物品被加载到装备栏时触发
 *
 * @constructor Create empty Item load event
 * @property entity 实体
 * @property itemStack 物品
 */
class ItemLoadEvent(
    val entity: Entity,
    val itemStack: ItemStack,
) : CancellableEvent {
    private var isCancelled = false
    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(p0: Boolean) {
        isCancelled = p0
    }
}