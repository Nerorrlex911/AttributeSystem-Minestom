package com.github.zimablue.attrsystem.api.event

import com.github.zimablue.attrsystem.api.compiled.sub.ComplexCompiledData
import net.minestom.server.entity.LivingEntity
import net.minestom.server.event.trait.CancellableEvent
import net.minestom.server.item.ItemStack

/**
 * 读取物品事件
 *
 * @constructor Create empty Item read event
 * @property entity 实体
 * @property itemStack 物品
 * @property compiledData 预编译属性数据
 */
class ItemReadEvent(
    val entity: LivingEntity?,
    val itemStack: ItemStack,
    val compiledData: ComplexCompiledData,
    val slot: String?,
) : CancellableEvent {
    private var isCancelled = false
    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(p0: Boolean) {
        isCancelled = p0
    }
}