package com.github.zimablue.attrsystem.internal.core.equipment

import com.github.zimablue.attrsystem.api.equipment.EquipmentLoader
import com.github.zimablue.attrsystem.internal.manager.SlotManager
import com.github.zimablue.devoutserver.util.map.LowerMap
import net.minestom.server.entity.EquipmentSlot
import net.minestom.server.entity.LivingEntity
import net.minestom.server.item.ItemStack

object NormalEquipmentLoader : EquipmentLoader<LivingEntity> {

    private val slots = LowerMap<EquipmentSlot>()

    override val key: String = "default"

    override val priority: Int = 1000

    override fun loadEquipment(entity: LivingEntity): Map<String, ItemStack?> {
        val items = HashMap<String, ItemStack?>()
        for ((key, equipmentSlot) in slots) {
            items[key] = entity.getEquipment(equipmentSlot)
        }
        return items
    }

    fun onEnable() {
        onReload()
    }

    fun onReload() {
        slots.clear()
        slots.putAll(
            mapOf(
                "头盔" to EquipmentSlot.HELMET,
                "胸甲" to EquipmentSlot.CHESTPLATE,
                "护腿" to EquipmentSlot.LEGGINGS,
                "靴子" to EquipmentSlot.BOOTS,
                "主手" to EquipmentSlot.MAIN_HAND,
                "副手" to EquipmentSlot.OFF_HAND
            )
        )
        val entityConfig = SlotManager.slotConfig.getConfigurationSection("entity")!!
        for (key in entityConfig.getKeys(false)) {
            val slotName = entityConfig.getString(key) ?: continue
            val slot = kotlin.runCatching { EquipmentSlot.valueOf(slotName.uppercase()) }.getOrNull()
            if (slot == null) {
                println("§c[AttributeSystem] §4Unknown equipment slot type: $slotName (slot: $key)")
                continue
            }
            slots.register(key, slot)
        }
    }
}