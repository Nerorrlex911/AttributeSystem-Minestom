package com.github.zimablue.attrsystem.api.manager

import com.github.zimablue.attrsystem.api.compiled.CompiledAttrDataCompound
import com.github.zimablue.attrsystem.api.compiled.CompiledData
import com.github.zimablue.devoutserver.util.map.BaseMap
import net.minestom.server.entity.LivingEntity
import java.util.*

/**
 * Attribute data manager
 *
 * @constructor Create empty Attribute data manager
 */
abstract class CompiledAttrDataManager : BaseMap<UUID, CompiledAttrDataCompound>(){

    /**
     * 是否有此预编译数据
     *
     * @param entity LivingEntity
     * @param source String 源
     * @return Boolean
     */
    abstract fun hasCompiledData(entity: LivingEntity, source: String): Boolean


    /**
     * 是否有此预编译数据
     *
     * @param uuid UUID
     * @param source String 源
     * @return Boolean
     */
    abstract fun hasCompiledData(uuid: UUID, source: String): Boolean


    /**
     * 给实体添加预编译属性数据
     *
     * @param entity 实体
     * @param source 源
     * @param attributes 字符串集(会据此读取出预编译属性数据)
     * @return 预编译属性数据
     */

    abstract fun addCompiledData(
        entity: LivingEntity,
        source: String,
        attributes: Collection<String>, slot: String? = null,
    ): CompiledData?

    /**
     * 给实体添加预编译属性数据
     *
     * @param entity 实体
     * @param source 源
     * @param compiledData 预编译属性数据
     * @return 预编译属性数据
     */

    abstract fun addCompiledData(
        entity: LivingEntity, source: String, compiledData: CompiledData,
    ): CompiledData

    /**
     * 给实体添加预编译属性数据
     *
     * @param uuid UUID
     * @param source 源
     * @param attributes 字符串集(会据此读取出预编译属性数据)
     * @return 预编译属性数据
     */

    abstract fun addCompiledData(
        uuid: UUID, source: String, attributes: Collection<String>, slot: String? = null,
    ): CompiledData?

    /**
     * 给实体添加预编译属性数据
     *
     * @param uuid UUID
     * @param source 源
     * @param compiledData 预编译属性数据
     * @return 预编译属性数据
     */

    abstract fun addCompiledData(
        uuid: UUID, source: String, compiledData: CompiledData,
    ): CompiledData?


    /**
     * 根据 键(源) 删除实体的预编译属性数据
     *
     * @param entity 实体
     * @param source 键(源)
     */
    abstract fun removeCompiledData(entity: LivingEntity, source: String): CompiledData?

    /**
     * 根据 键(源) 删除实体的预编译属性数据
     *
     * @param uuid UUID
     * @param source 键(源)
     */
    abstract fun removeCompiledData(uuid: UUID, source: String): CompiledData?

    /**
     * 删除所有以 所给前缀 为开头的 预编译属性数据
     *
     * @param uuid UUID
     * @param prefix String 前缀
     */
    abstract fun removeIfStartWith(uuid: UUID, prefix: String)

    /**
     * 删除所有以 所给前缀 为开头的 预编译属性数据
     *
     * @param entity LivingEntity
     * @param prefix String 前缀
     */
    abstract fun removeIfStartWith(entity: LivingEntity, prefix: String)

    abstract override fun get(key: UUID): CompiledAttrDataCompound?
}
