package com.github.zimablue.attrsystem.api.manager

import com.github.zimablue.attrsystem.api.attribute.compound.AttributeData
import com.github.zimablue.attrsystem.api.attribute.compound.AttributeDataCompound
import com.github.zimablue.devoutserver.util.map.BaseMap
import net.minestom.server.entity.LivingEntity
import java.util.*

/**
 * Attribute data manager
 *
 * @constructor Create empty Attribute data manager
 */
abstract class AttributeDataManager : BaseMap<UUID, AttributeDataCompound>() {

    /**
     * 更新实体的属性数据
     *
     * @param entity 实体
     * @return 属性数据集
     */
    abstract fun update(entity: LivingEntity): AttributeDataCompound?


    /**
     * 给实体添加属性数据
     *
     * @param entity 实体
     * @param source 源
     * @param attributeData 属性数据
     * @return 属性数据
     */

    abstract fun addAttrData(
        entity: LivingEntity, source: String, attributeData: AttributeData,
    ): AttributeData

    /**
     * 给实体添加属性数据
     *
     * @param uuid UUID
     * @param source 源
     * @param attributeData 属性数据
     * @return 属性数据
     */

    abstract fun addAttrData(
        uuid: UUID, source: String, attributeData: AttributeData,
    ): AttributeData

    /**
     * 根据 源 删除实体的属性数据
     *
     * @param entity 实体
     * @param source 源
     */
    abstract fun removeAttrData(entity: LivingEntity, source: String): AttributeData?

    /**
     * 根据 源 删除实体的属性数据
     *
     * @param uuid UUID
     * @param source 源
     */
    abstract fun removeAttrData(uuid: UUID, source: String): AttributeData?

    override fun get(key: UUID): AttributeDataCompound? {
        return super.get(key)
    }
}
