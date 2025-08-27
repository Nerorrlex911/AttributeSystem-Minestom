package com.github.zimablue.attrsystem.api.equipment

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.internal.core.equipment.NormalEquipmentLoader
import com.github.zimablue.attrsystem.internal.core.equipment.PlayerEquipmentLoader
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import com.github.zimablue.devoutserver.util.map.component.Registrable
import net.minestom.server.entity.LivingEntity
import net.minestom.server.item.ItemStack

/**
 * @className EquipmentLoader
 *
 * @author Glom
 * @date 2023/1/22 9:46 Copyright 2023 user. All rights reserved.
 */
interface EquipmentLoader<E : LivingEntity> : Registrable<String>, Comparable<EquipmentLoader<*>> {
    override val key: String
    fun filter(entity: LivingEntity): Boolean = false
    fun loadEquipment(entity: E): Map<String, ItemStack?>

    val priority: Int

    override fun compareTo(other: EquipmentLoader<*>): Int = if (this.priority == other.priority) 0
    else if (this.priority > other.priority) 1
    else -1

    override fun register() {
        AttributeSystem.equipmentDataManager.registerLoader(this as EquipmentLoader<in LivingEntity>)
    }

    companion object {
        @Awake(PluginLifeCycle.LOAD)
        fun onLoad() {
            NormalEquipmentLoader.register()
            PlayerEquipmentLoader.register()
        }
    }
}