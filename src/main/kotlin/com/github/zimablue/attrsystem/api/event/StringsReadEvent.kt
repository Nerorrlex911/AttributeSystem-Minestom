package com.github.zimablue.attrsystem.api.event

import com.github.zimablue.attrsystem.api.compiled.CompiledData
import net.minestom.server.entity.LivingEntity
import net.minestom.server.event.trait.CancellableEvent

/**
 * 读取字符串属性事件
 *
 * @constructor Create empty Strings read event
 * @property entity 实体
 * @property strings 字符串集
 * @property compiledData 预编译属性数据
 */
open class StringsReadEvent(
    val entity: LivingEntity?,
    val strings: Collection<String>,
    val compiledData: CompiledData,
) : CancellableEvent {
    var isCancelled = false
    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(p0: Boolean) {
        isCancelled = p0
    }

}