package com.github.zimablue.attrsystem.fight.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.fight.api.event.FightEvent
import com.github.zimablue.attrsystem.fight.internal.core.listener.Attack.defaultFightGroup
import com.github.zimablue.attrsystem.internal.manager.ASConfig
import com.github.zimablue.attrsystem.utils.getName
import com.github.zimablue.attrsystem.utils.sendLang
import com.github.zimablue.devoutserver.feature.luckperms.LuckPerms.hasPermission
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import net.minestom.server.entity.EquipmentSlot
import net.minestom.server.entity.Player
import net.minestom.server.item.Material

object DisableAttackMaterialRealizer  {

    val values: List<String>
        get() = ASConfig.options.getStringList("values")

    private val disableDamageTypes = HashSet<Material>()

    @Awake(PluginLifeCycle.ENABLE)
    fun onEnable() {
        onReload()
        AttributeSystem.asEventNode.addListener(FightEvent.Pre::class.java) { event ->
            disableMaterialAttack(event)
        }
    }

    fun disableMaterialAttack(event: FightEvent.Pre) {
        val attacker = event.fightData.attacker ?: return
        if (attacker !is Player || event.fightData["projectile"] == "true" || event.key != defaultFightGroup) return
        val material = attacker.getEquipment(EquipmentSlot.MAIN_HAND).material() ?: return
        if (attacker.hasPermission("as.damage_type.${material.name().lowercase()}")) return
        if (disableDamageTypes.contains(material)) {
            event.isCancelled = true
            attacker.sendLang("disable-damage-type", attacker.getEquipment(EquipmentSlot.MAIN_HAND).getName())
            return
        }
    }

    @Awake(PluginLifeCycle.RELOAD)
    fun onReload() {
        disableDamageTypes.clear()
        for (material in values) {
            val materialMC = Material.fromKey(material.uppercase())
            disableDamageTypes.add(materialMC ?: continue)

        }
    }

}