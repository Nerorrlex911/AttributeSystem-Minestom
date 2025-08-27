package com.github.zimablue.attrsystem.internal.core.equipment

import com.github.zimablue.attrsystem.api.equipment.EquipmentLoader
import com.github.zimablue.attrsystem.internal.manager.SlotManager
import com.github.zimablue.attrsystem.utils.hasLore
import com.github.zimablue.devoutserver.util.map.LowerKeyMap
import com.github.zimablue.devoutserver.util.map.component.Registrable
import net.minestom.server.entity.EquipmentSlot
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import taboolib.common5.Coerce
import taboolib.common5.cint

object PlayerEquipmentLoader : EquipmentLoader<Player> {

    private val slots = LowerKeyMap<PlayerSlot>()

    override val key: String = "default"

    override val priority: Int = 999

    override fun filter(entity: LivingEntity): Boolean {
        return entity is Player
    }

    override fun loadEquipment(entity: Player): Map<String, ItemStack?> {
        val items = HashMap<String, ItemStack?>()
        for ((slot, playerSlot) in slots) {
            items[slot] = playerSlot.getItem(entity)
        }
        return items
    }

    fun onEnable() {
        onReload()
    }
    fun onReload() {
        slots.clear()
        val playerConfig = SlotManager.slotConfig.getConfigurationSection("player")!!
        for (key in playerConfig.getKeys(false)) {
            val value = playerConfig[key]
            val slot: String
            var require: String? = null
            if (value is Map<*, *>) {
                val section = value as Map<String, Any>
                slot = section["slot"].toString()
                require = section["require"]?.toString()
            } else {
                slot = value.toString()
            }
            slots.register(PlayerSlot(key, slot.uppercase(), require))
        }

        setOf(
            PlayerSlot("头盔", "HELMET"),
            PlayerSlot("胸甲", "CHESTPLATE"),
            PlayerSlot("护腿", "LEGGINGS"),
            PlayerSlot("靴子", "BOOTS"),
            PlayerSlot("主手", "MAIN_HAND"),
            PlayerSlot("副手", "OFF_HAND")
        ).forEach(slots::register)
    }

    /**
     * Player slot
     *
     * @constructor Create empty Player slot
     * @property key 槽位键
     * @property slot 槽位 ( EquipmentSlot 或 数字)
     */
    data class PlayerSlot(override val key: String, val slot: String, val require: String? = null) :
        Registrable<String> {
        val equipment: EquipmentSlot? =
            if (!Coerce.asInteger(slot).isPresent)
                EquipmentSlot.valueOf(slot.uppercase())
            else null

        fun getSlot(player: Player): Int {
            return if (slot == "held") player.heldSlot.cint else slot.cint
        }

        fun getItem(player: Player): ItemStack? {
            return (if (equipment == null) {
                player.inventory.getItemStack(getSlot(player))
            } else {
                player.getEquipment(equipment)
            }).let {
                if (require != null && !it.hasLore(require)) null else it
            }
        }

        override fun register() {
            slots.register(key, this)
        }
    }
}