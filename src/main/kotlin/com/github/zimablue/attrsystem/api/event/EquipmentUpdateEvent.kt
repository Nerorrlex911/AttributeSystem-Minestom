package com.github.zimablue.attrsystem.api.event

import com.github.zimablue.attrsystem.api.equipment.EquipmentDataCompound
import net.minestom.server.entity.Entity
import net.minestom.server.event.Event
import net.minestom.server.event.trait.CancellableEvent

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
    ) : CancellableEvent {
        private var isCancelled = false
        override fun isCancelled(): Boolean = isCancelled
        override fun setCancelled(cancel: Boolean) {
            isCancelled = cancel
        }
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
    ) : CancellableEvent {
        private var isCancelled = false
        override fun isCancelled(): Boolean = isCancelled
        override fun setCancelled(cancel: Boolean) {
            isCancelled = cancel
        }
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
    ) : CancellableEvent {
        private var isCancelled = false
        override fun isCancelled(): Boolean = isCancelled
        override fun setCancelled(cancel: Boolean) {
            isCancelled = cancel
        }
    }

}
