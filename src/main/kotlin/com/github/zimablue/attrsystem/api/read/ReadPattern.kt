package com.github.zimablue.attrsystem.api.read

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.api.attribute.Attribute
import com.github.zimablue.attrsystem.api.operation.Operation
import com.github.zimablue.attrsystem.api.read.status.Status
import com.github.zimablue.devoutserver.util.map.LowerMap
import com.github.zimablue.devoutserver.util.map.component.Registrable
import net.kyori.adventure.text.Component
import net.minestom.server.entity.LivingEntity

/**
 * Read pattern
 *
 * 读取格式
 *
 * 你可以通过拓展该类来实现自己的读取格式 （记得注册）
 *
 * 对应的属性状态可以用自带的或者自己拓展
 *
 * 读取时不用考虑是否符合条件 已经处理过了
 *
 * @constructor Create empty Read pattern
 * @property key 读取格式键
 */
abstract class ReadPattern<A : Any>(
    override val key: String,
) : Registrable<String> {
    /** 是否在重载时删除 */
    var release = false

    /**
     * Status中可能的键
     *
     * @return Set<String>
     */
    abstract fun operations(): LowerMap<Operation<A>>

    /**
     * Read
     *
     * 读取单行字符串
     *
     * @param string 字符串
     * @param attribute 属性
     * @param entity 实体
     * @param slot 槽位
     * @return 读取结果-属性状态
     */
    abstract fun read(
        string: String,
        attribute: Attribute,
        entity: LivingEntity?,
        slot: String?,
    ): Status<A>?

    /**
     * Read 读取单行字符串 (无槽位)
     *
     * @param string 字符串
     * @param attribute 属性
     * @param entity 实体
     * @return 读取结果-属性状态
     */
    fun read(
        string: String,
        attribute: Attribute,
        entity: LivingEntity?,
    ): Status<A>? {
        return read(string, attribute, entity, "null")
    }

    /**
     * Read NBT
     *
     * @param map NBT
     * @param attribute 属性
     * @return 读取结果-属性状态
     */
    abstract fun readNBT(
        map: Map<String, Any>,
        attribute: Attribute,
    ): Status<A>?

    /**
     * Placeholder
     *
     * 占位符(读取组内部的，会在 %as_att:属性键_占位符键% 生效)
     *
     * @param key 占位符键
     * @param attribute 属性
     * @param status 属性状态
     * @param entity 实体
     * @return 返回值
     */
    abstract fun placeholder(
        key: String,
        attribute: Attribute,
        status: Status<*>,
        entity: LivingEntity? = null,
    ): A?

    /**
     * 用于指令统计信息 可以直接返回 TellrawJson() 空对象
     *
     * @param attribute 属性
     * @param status 属性状态
     * @param entity 实体
     * @return ComponentText
     */
    open fun stat(
        attribute: Attribute,
        status: Status<*>,
        entity: LivingEntity?,
    ): Component {
        return Component.empty()
    }

    open fun getTotal(attribute: Attribute, status: Status<*>, entity: LivingEntity?): A? =
        placeholder("total", attribute, status, entity)

    open fun getMin(attribute: Attribute, status: Status<*>, entity: LivingEntity?): A? =
        placeholder("min", attribute, status, entity)

    open fun getMax(attribute: Attribute, status: Status<*>, entity: LivingEntity?): A? =
        placeholder("max", attribute, status, entity)

    override fun register() {
        AttributeSystem.readPatternManager.register(this)
    }
}
