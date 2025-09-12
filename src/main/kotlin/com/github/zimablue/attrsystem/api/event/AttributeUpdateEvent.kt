package com.github.zimablue.attrsystem.api.event

import com.github.zimablue.attrsystem.api.attribute.compound.AttributeDataCompound
import net.minestom.server.entity.Entity
import net.minestom.server.event.trait.EntityEvent

class AttributeUpdateEvent {
    /**
     * 属性更新前
     *
     * @property entity 实体
     * @property data 属性数据集
     */
    class Pre(
        private val entity: Entity,
        val data: AttributeDataCompound,
    ) : EntityEvent {
        override fun getEntity(): Entity {
            return entity
        }
    }

    /**
     * 属性更新中 此时新的属性数据已经加载 但属性映射还没有计算
     *
     * @property entity 实体
     * @property data 属性数据集
     */
    class Process(
        private val entity: Entity,
        val data: AttributeDataCompound,
    ) : EntityEvent {
        override fun getEntity(): Entity {
            return entity
        }
    }

    /**
     * 属性更新后 完全新的属性数据 属性映射已计算
     *
     * @property entity 实体
     * @property data 属性数据集
     */
    class Post(
        private val entity: Entity,
        val data: AttributeDataCompound,
    ) : EntityEvent {
        override fun getEntity(): Entity {
            return entity
        }
    }
}
