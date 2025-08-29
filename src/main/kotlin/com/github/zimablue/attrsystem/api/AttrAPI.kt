package com.github.zimablue.attrsystem.api

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.AttributeSystem.attributeManager
import com.github.zimablue.attrsystem.AttributeSystem.attributeSystemAPI
import com.github.zimablue.attrsystem.AttributeSystem.operationManager
import com.github.zimablue.attrsystem.api.attribute.Attribute
import com.github.zimablue.attrsystem.api.attribute.compound.AttributeData
import com.github.zimablue.attrsystem.api.attribute.compound.AttributeDataCompound
import com.github.zimablue.attrsystem.api.compiled.CompiledData
import com.github.zimablue.attrsystem.api.equipment.EquipmentData
import com.github.zimablue.attrsystem.api.equipment.EquipmentDataCompound
import com.github.zimablue.attrsystem.api.operation.Operation
import com.github.zimablue.attrsystem.utils.validEntity
import net.minestom.server.entity.LivingEntity
import net.minestom.server.item.ItemStack
import java.util.*

object AttrAPI {

    /**
     * EntityUpdate
     *
     * 更新实体(装备 属性 原版属性实现)
     *
     * 原版属性不会立即生效
     *
     * 建议异步调用
     *
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.update() {
        attributeSystemAPI.update(this)
    }

    /**
     * EntityUpdate
     *
     * 更新实体(装备 属性 原版属性实现)
     *
     * 同步版本，原版属性会立即生效
     *
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.updateSync() {
        TODO()//if (isPrimaryThread) update() else sync { update() }
    }

    /**
     * 获取Operation<*>对象
     *
     * @param key String Operation名称
     * @return Operation<*>?
     */
    @JvmStatic
    fun operation(key: String): Operation<*>? {
        return operationManager[key]
    }


    /**
     * 获取属性，通过属性key或属性名
     *
     * @param key String 属性key或属性名
     * @return Attribute 属性
     */
    @JvmStatic
    fun attribute(key: String): Attribute? {
        return attributeManager[key]
    }

    /**
     * 获取属性数据集
     *
     * @return AttributeDataCompound? 属性数据集
     * @receiver UUID 实体uuid
     */
    @JvmStatic
    fun UUID.getAttrData(): AttributeDataCompound? {
        return AttributeSystem.attributeDataManager[this]
    }

    /**
     * 获取属性数据集
     *
     * @return AttributeDataCompound? 属性数据集
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.getAttrData(): AttributeDataCompound? {
        return uuid.getAttrData()
    }

    /**
     * 获取装备数据集
     *
     * @return EquipmentDataCompound? 装备数据集
     * @receiver UUID 实体uuid
     */
    @JvmStatic
    fun UUID.getEquipData(): EquipmentDataCompound? {
        return AttributeSystem.equipmentDataManager[this]
    }

    /**
     * 获取装备数据集
     *
     * @return EquipmentDataCompound? 装备数据集
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.getEquipData(): EquipmentDataCompound? {
        return uuid.getEquipData()
    }

    /**
     * 给实体添加属性数据
     *
     * @param source String 键(源)
     * @param attributeData AttributeData 属性数据
     * @return AttributeData 属性数据
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.addAttrData(
        source: String, attributeData: AttributeData,
    ): AttributeData {
        return AttributeSystem.attributeDataManager.addAttrData(this, source, attributeData)
    }

    /**
     * 给实体添加属性数据
     *
     * @param source String 键(源)
     * @param attributeData AttributeData 属性数据
     * @return AttributeData 属性数据
     * @receiver UUID 实体uuid
     */
    @JvmStatic
    fun UUID.addAttrData(
        source: String, attributeData: AttributeData,
    ): AttributeData {
        return AttributeSystem.attributeDataManager.addAttrData(this, source, attributeData)
    }

    /**
     * 根据 键(源) 删除实体的属性数据
     *
     * @param source String 键(源)
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.removeAttrData(source: String) {
        AttributeSystem.attributeDataManager.removeAttrData(this, source)
    }

    /**
     * 根据 键(源) 删除实体的属性数据
     *
     * @param source String
     * @receiver UUID
     */
    @JvmStatic
    fun UUID.removeAttrData(source: String) {
        AttributeSystem.attributeDataManager.removeAttrData(this, source)
    }


    /**
     * 读取物品lore上的属性
     *
     * @param entity LivingEntity? 实体
     * @param slot String? 槽位
     * @return AttributeData? 属性数据
     * @receiver ItemStack 物品
     */
    @JvmStatic
    fun ItemStack.readItemLore(
        entity: LivingEntity? = null, slot: String? = null,
    ): CompiledData? {
        return AttributeSystem.readManager.readItemLore(this, entity, slot)
    }

    /**
     * 读取物品lore上的属性
     *
     * @param entity LivingEntity? 实体
     * @param slot String? 槽位
     * @return AttributeData? 属性数据
     * @receiver Collection<ItemStack> 物品集合
     */
    @JvmStatic
    fun Collection<ItemStack>.readItemsLore(
        entity: LivingEntity? = null, slot: String? = null,
    ): CompiledData {
        return AttributeSystem.readManager.readItemsLore(this, entity, slot)
    }

    /**
     * 读取物品NBT上的属性
     *
     * @param entity LivingEntity? 实体
     * @param slot String? 槽位
     * @return AttributeDataCompound? 属性数据
     * @receiver ItemStack 物品
     */
    @JvmStatic
    fun ItemStack.readItemNBT(
        entity: LivingEntity? = null, slot: String? = null,
    ): CompiledData? {
        return AttributeSystem.readManager.readItemNBT(this, entity, slot)
    }

    /**
     * 读取物品NBT上的属性
     *
     * @param entity LivingEntity? 实体
     * @param slot String? 槽位
     * @return AttributeDataCompound? 属性数据
     * @receiver Collection<ItemStack> 物品集合
     */
    @JvmStatic
    fun Collection<ItemStack>.readItemsNBT(
        entity: LivingEntity? = null, slot: String? = null,
    ): CompiledData {
        return AttributeSystem.readManager.readItemsNBT(this, entity, slot)
    }

    /**
     * 读取物品属性 （lore 与 nbt） 都读
     *
     * @param entity LivingEntity? 实体
     * @param slot String? 槽位
     * @return AttributeDataCompound 属性数据
     * @receiver ItemStack 物品
     */
    @JvmStatic
    fun ItemStack.readItem(
        entity: LivingEntity? = null, slot: String? = null,
    ): CompiledData? {
        return AttributeSystem.readManager.readItem(this, entity, slot)
    }

    /**
     * 读取物品属性 （lore 与 nbt） 都读
     *
     * @param entity LivingEntity? 实体
     * @param slot String? 槽位
     * @return AttributeDataCompound 属性数据
     * @receiver Collection<ItemStack> 物品集合
     */
    @JvmStatic
    fun Collection<ItemStack>.readItems(
        entity: LivingEntity? = null, slot: String? = null,
    ): CompiledData {
        return AttributeSystem.readManager.readItems(this, entity, slot)
    }

    /**
     * 读取字符串集合中的属性
     *
     * @param entity LivingEntity? 实体
     * @param slot String? 槽位
     * @return AttributeData 属性数据
     * @receiver Collection<String> 字符串集合
     */
    @JvmStatic
    fun Collection<String>.read(entity: LivingEntity? = null, slot: String? = null): CompiledData? {
        return AttributeSystem.readManager.read(this, entity, slot)
    }



    /**
     * 给实体添加装备数据
     *
     * @param source 键(源)
     * @param equipments 装备数据（槽位 to 物品）
     * @return 装备数据
     * @receiver entity 实体
     */
    @JvmStatic
    fun LivingEntity.addEquipData(
        source: String,
        equipments: Map<String, ItemStack>,
    ): EquipmentData {
        return AttributeSystem.equipmentDataManager.addEquipData(this, source, equipments)
    }

    /**
     * 给实体添加装备数据
     *
     * @param source 键(源)
     * @param equipmentData 装备数据
     * @return 装备数据
     * @receiver entity 实体
     */
    @JvmStatic
    fun LivingEntity.addEquipData(
        source: String, equipmentData: EquipmentData,
    ): EquipmentData {
        return AttributeSystem.equipmentDataManager.addEquipData(this, source, equipmentData)
    }


    /**
     * 根据 键(源) 删除实体的装备数据
     *
     * @param source 键(源)
     * @return 装备数据
     * @receiver entity 实体
     */
    @JvmStatic
    fun LivingEntity.removeEquipData(
        source: String,
    ) {
        AttributeSystem.equipmentDataManager.removeEquipData(this, source)
    }

    /**
     * 给实体添加装备数据
     *
     * @param source 键(源)
     * @param equipments 装备数据（槽位 to 物品）
     * @return 装备数据
     * @receiver UUID 实体的uuid
     */
    @JvmStatic
    fun UUID.addEquipData(
        source: String,
        equipments: Map<String, ItemStack>,
    ): EquipmentData? {
        return AttributeSystem.equipmentDataManager.addEquipData(this, source, equipments)
    }

    /**
     * 给实体添加装备数据
     *
     * @param source 键(源)
     * @param equipmentData 装备数据
     * @return 装备数据
     * @receiver UUID 实体的uuid
     */
    @JvmStatic
    fun UUID.addEquipData(
        source: String, equipmentData: EquipmentData,
    ): EquipmentData? {
        return AttributeSystem.equipmentDataManager.addEquipData(this, source, equipmentData)
    }


    /**
     * 根据 键(源) 删除实体的装备数据
     *
     * @param source 键(源)
     * @return 装备数据
     * @receiver UUID 实体的uuid
     */
    @JvmStatic
    fun UUID.removeEquipData(
        source: String,
    ) {
        AttributeSystem.equipmentDataManager.removeEquipData(this, source)
    }


    /**
     * 判断实体是否有属性数据
     *
     * @return Boolean 是否有属性数据
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.hasData(): Boolean = AttributeSystem.attributeDataManager.containsKey(uuid)


    /**
     * 给实体添加预编译属性数据
     *
     * @param source 源
     * @param attributes 字符串集(会据此读取出预编译属性数据)
     * @return 预编译属性数据
     * @receiver entity 实体
     */
    @JvmStatic
    fun LivingEntity.addCompiledData(
        source: String,
        attributes: Collection<String>,
    ): CompiledData? = uuid.addCompiledData(source, attributes)

    /**
     * 给实体添加预编译属性数据
     *
     * @param source 源
     * @param compiledData 预编译属性数据
     * @return 预编译属性数据
     * @receiver entity 实体
     */
    @JvmStatic
    fun LivingEntity.addCompiledData(
        source: String, compiledData: CompiledData,
    ): CompiledData? = uuid.addCompiledData(source, compiledData)

    /**
     * 给实体添加预编译属性数据
     *
     * @param source 源
     * @param attributes 字符串集(会据此读取出预编译属性数据)
     * @return 预编译属性数据
     * @receiver uuid UUID
     */
    @JvmStatic
    fun UUID.addCompiledData(
        source: String, attributes: Collection<String>,
    ): CompiledData? {
        return AttributeSystem.compiledAttrDataManager.addCompiledData(this, source, attributes)
    }

    /**
     * 给实体添加预编译属性数据
     *
     * @param source 源
     * @param compiledData 预编译属性数据
     * @return 预编译属性数据
     * @receiver uuid UUID
     */
    @JvmStatic
    fun UUID.addCompiledData(
        source: String, compiledData: CompiledData,
    ): CompiledData? {
        return AttributeSystem.compiledAttrDataManager.addCompiledData(this, source, compiledData)
    }


    /**
     * 根据 键(源) 删除实体的预编译属性数据
     *
     * @param source 键(源)
     * @receiver entity 实体
     */
    @JvmStatic
    fun LivingEntity.removeCompiledData(source: String): CompiledData? {
        return uuid.removeCompiledData(source)
    }

    /**
     * 根据 键(源) 删除实体的预编译属性数据
     *
     * @param source 键(源)
     * @receiver uuid UUID
     */
    @JvmStatic
    fun UUID.removeCompiledData(source: String): CompiledData? {
        return AttributeSystem.compiledAttrDataManager.removeCompiledData(this, source)
    }
    
}