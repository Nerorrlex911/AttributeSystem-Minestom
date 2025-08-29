package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.AttributeSystem.compiledAttrDataManager
import com.github.zimablue.attrsystem.AttributeSystem.equipmentDataManager
import com.github.zimablue.attrsystem.api.AttrAPI.readItem
import com.github.zimablue.attrsystem.api.compiled.sub.ComplexCompiledData
import com.github.zimablue.attrsystem.api.equipment.EquipmentData
import com.github.zimablue.attrsystem.api.equipment.EquipmentDataCompound
import com.github.zimablue.attrsystem.api.equipment.EquipmentLoader
import com.github.zimablue.attrsystem.api.event.EquipmentUpdateEvent
import com.github.zimablue.attrsystem.api.event.ItemLoadEvent
import com.github.zimablue.attrsystem.api.manager.EquipmentDataManager
import com.github.zimablue.attrsystem.internal.core.equipment.NormalEquipmentLoader
import com.github.zimablue.attrsystem.utils.isAlive
import com.github.zimablue.attrsystem.utils.validEntity
import net.minestom.server.entity.LivingEntity
import net.minestom.server.event.EventDispatcher
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag
import java.util.*

object EquipmentDataManagerImpl : EquipmentDataManager() {


    override fun getSource(source: String?, slot: String?) =
        "!!Equipment" + (if (source != null) "-$source" else "") + (if (slot != null) "-$slot" else "")

    override fun get(key: UUID): EquipmentDataCompound? {
        return uncheckedGet(key)
    }

    private fun uncheckedGet(key: UUID): EquipmentDataCompound? {
        return super.get(key)
    }

    override fun register(key: UUID, value: EquipmentDataCompound): EquipmentDataCompound? {
        return super.register(key, value.apply { entity = key.validEntity() })
    }

    private val loaders = ArrayList<EquipmentLoader<in LivingEntity>>()

    override fun registerLoader(loader: EquipmentLoader<in LivingEntity>) {
        loaders += loader
        loaders.sorted()
    }

    override fun update(entity: LivingEntity): EquipmentDataCompound? {
        if (!entity.isAlive()) return null
        val uuid = entity.uuid
        var data = uncheckedGet(uuid) ?: EquipmentDataCompound(entity)
        equipmentDataManager.register(uuid, data)
        val pre = EquipmentUpdateEvent.Pre(entity, data)
        AttributeSystem.asEventNode.call(pre)
        //不允许取消的事件还检测isCancelled，这不是脱裤子放屁吗？
//        if (pre.isCancelled) {
//            return data
//        }
        data = pre.data

        entity.loadEquipments(data)

        val postEvent = EquipmentUpdateEvent.Post(entity, data)
        AttributeSystem.asEventNode.call(postEvent)
//        if (postEvent.isCancelled) {
//            return data
//        }
        data = postEvent.data
        equipmentDataManager.register(uuid, data)
        return data
    }

    private fun LivingEntity.loadEquipments(data: EquipmentDataCompound) {
        val equipments =
            (loaders.firstOrNull { it.filter(this) }
                ?: NormalEquipmentLoader).loadEquipment(this)

        for ((slot, item) in equipments) {
            data.addEquipment(this, BASE_EQUIPMENT_KEY, slot, item)
        }
    }

    private const val BASE_EQUIPMENT_KEY = "BASE-EQUIPMENT"
    private const val IGNORE_KEY = "IGNORE_ATTRIBUTE"

    private fun EquipmentDataCompound.addEquipment(
        entity: LivingEntity,
        source: String,
        slot: String,
        item: ItemStack?,
        condition: (ItemStack) -> Boolean = { true },
    ): EquipmentData? {
        if (item == null || item.isAir() || !condition(item)) {
            removeItem(source, slot)
            return null
        }
        val event = ItemLoadEvent(entity, item)
        AttributeSystem.asEventNode.call(event)
        if (event.isCancelled) {
            removeItem(source, slot)
            return null
        }
        val eventItem = event.itemStack
        if (eventItem.isAir() || !condition(eventItem)) {
            removeItem(source, slot)
            return null
        }
        val compiledSource = getSource(source, slot)


        if (!hasChanged(eventItem, source, slot) && compiledAttrDataManager.hasCompiledData(
                entity,
                compiledSource
            )
        ) {
            return null
        }

        if (eventItem.hasTag(Tag.NBT(IGNORE_KEY))) {
            removeItem(source, slot)
            return null
        }
        compiledAttrDataManager.addCompiledData(
            entity.uuid,
            compiledSource,
            eventItem.readItem(entity, slot) ?: ComplexCompiledData()
        )
        if (eventItem.isAir) {
            removeItem(source, slot)
            return null
        }
        return computeIfAbsent(source) { EquipmentData(this@addEquipment, source) }.apply {
            uncheckedPut(
                slot,
                eventItem
            )
        }
    }

    override fun addEquipment(
        entity: LivingEntity,
        source: String,
        slot: String,
        itemStack: ItemStack,
    ): EquipmentData? {
        val uuid = entity.uuid
        return computeIfAbsent(uuid) { EquipmentDataCompound().apply { this.entity = entity } }.addEquipment(
            entity,
            source,
            slot,
            itemStack
        )
    }

    override fun addEquipData(entity: LivingEntity, source: String, equipments: Map<String, ItemStack>): EquipmentData {
        val uuid = entity.uuid
        return computeIfAbsent(uuid) { EquipmentDataCompound().apply { this.entity = entity } }.let { compound ->
            compound.computeIfAbsent(source) { EquipmentData(compound, source) }.apply {
                val newKeys = equipments.keys
                filter { it.key !in newKeys }.map { it.key }.forEach(this::remove)
                equipments.forEach { (slot, item) ->
                    compound.addEquipment(entity, source, slot, item)
                }
            }
        }
    }


    override fun removeEquipData(entity: LivingEntity, source: String): EquipmentData? {
        return removeEquipData(entity.uuid, source)
    }

    override fun removeItem(entity: LivingEntity, source: String, slot: String): ItemStack? {
        return removeItem(entity.uuid, source, slot)
    }

    override fun clearEquipData(entity: LivingEntity) {
        clearEquipData(entity.uuid)
    }

    override fun clearEquipData(entity: LivingEntity, source: String) {
        clearEquipData(entity.uuid, source)
    }

    override fun addEquipData(uuid: UUID, source: String, slot: String, itemStack: ItemStack): EquipmentData? {
        return uuid.validEntity()?.let { addEquipment(it, source, slot, itemStack) }
    }

    override fun addEquipData(uuid: UUID, source: String, equipments: Map<String, ItemStack>): EquipmentData? {
        return uuid.validEntity()?.let { addEquipData(it, source, equipments) }
    }

    override fun removeEquipData(uuid: UUID, source: String): EquipmentData? {
        compiledAttrDataManager.removeIfStartWith(uuid, getSource(source))
        return get(uuid)?.uncheckedRemove(source)
    }

    override fun removeItem(uuid: UUID, source: String, slot: String): ItemStack? {
        val compound = this[uuid] ?: return null
        val data = compound[source] ?: return null
        compiledAttrDataManager.removeCompiledData(uuid, getSource(source, slot))
        return data.uncheckedRemove(slot)
    }

    override fun clearEquipData(uuid: UUID, source: String) {
        compiledAttrDataManager.removeIfStartWith(uuid, getSource(source))
        get(uuid)?.get(source)?.uncheckedClear()
    }

    override fun clearEquipData(uuid: UUID) {
        compiledAttrDataManager.removeIfStartWith(uuid, getSource())
        get(uuid)?.uncheckedClear()
    }

    override fun put(key: UUID, value: EquipmentDataCompound): EquipmentDataCompound? {
        return super.put(key, value)?.apply { entity = key.validEntity() }
    }
}
