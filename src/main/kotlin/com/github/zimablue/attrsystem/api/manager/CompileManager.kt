package com.github.zimablue.attrsystem.api.manager

import com.github.zimablue.attrsystem.api.attribute.compound.AttributeData
import com.github.zimablue.attrsystem.api.compiled.sub.ComplexCompiledData
import com.github.zimablue.attrsystem.api.compiled.sub.NBTCompiledData
import com.github.zimablue.attrsystem.api.compiled.sub.StringsCompiledData
import net.minestom.server.entity.LivingEntity

/**
 * Condition manager
 *
 * @constructor Create empty Condition manager
 */
abstract class CompileManager {
    /**
     * 构建 NBT预编译属性的构造器
     *
     * @param entity 实体
     * @param nbt NBT
     * @param slot 槽位
     * @return NBT条件的构造器
     */
    abstract fun compile(
        entity: LivingEntity?,
        nbt: Collection<Any>,
        slot: String? = null,
    ): (MutableMap<String, Any>) -> NBTCompiledData


    /**
     * 构建 字符串预编译属性的构造器
     *
     * @param entity 实体
     * @param string 字符串
     * @param slot 槽位
     * @return 字符串预编译属性的构造器，若无条件则返回null
     */
    abstract fun compile(
        entity: LivingEntity?,
        string: String,
        slot: String? = null,
    ): ((AttributeData) -> StringsCompiledData)?


    /**
     * 属性映射
     *
     * @param entity LivingEntity?
     * @return 预编译映射属性的构造器
     */
    abstract fun mapping(
        entity: LivingEntity?,
    ): (AttributeData) -> ComplexCompiledData

}
