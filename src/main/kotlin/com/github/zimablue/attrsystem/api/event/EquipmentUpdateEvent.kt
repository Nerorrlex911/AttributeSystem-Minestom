package com.github.zimablue.attrsystem.api.event

import com.github.zimablue.attrsystem.api.equipment.EquipmentDataCompound
import net.minestom.server.entity.Entity
import net.minestom.server.event.Event

class EquipmentUpdateEvent {

    /**
     * 装备更新前事件
     *
     * @property entity 实体
     * @property data 装备数据集
     */
    class Pre(
        val entity: Entity,
        val data: EquipmentDataCompound,
    ) : Event {


    }


    /**
     * 装备更新中事件
     *
     * @property entity 实体
     * @property data 装备数据集
     */
    class Process(
        val entity: Entity,
        val data: EquipmentDataCompound,
    ) : Event {


    }


    /**
     * 装备更新后事件
     *
     * @property entity 实体
     * @property data 装备数据集
     */
    class Post(
        val entity: Entity,
        val data: EquipmentDataCompound,
    ) : Event {


    }

}
