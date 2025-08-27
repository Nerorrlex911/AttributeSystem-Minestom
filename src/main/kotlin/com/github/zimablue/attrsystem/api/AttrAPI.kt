package com.github.zimablue.attrsystem.api

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.api.attribute.Attribute
import com.github.zimablue.attrsystem.api.compiled.CompiledData
import net.minestom.server.entity.LivingEntity
import net.minestom.server.item.ItemStack

object AttrAPI {
    /**
     * 获取属性，通过属性key或属性名
     *
     * @param key String 属性key或属性名
     * @return Attribute 属性
     */
    @JvmStatic
    fun attribute(key: String): Attribute? {
        return AttributeSystem.attributeManager[key]
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
}