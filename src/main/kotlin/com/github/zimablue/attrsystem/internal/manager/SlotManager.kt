package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.internal.core.equipment.NormalEquipmentLoader
import com.github.zimablue.attrsystem.internal.core.equipment.PlayerEquipmentLoader
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File

object SlotManager {
    lateinit var slotConfig: Configuration

    @Awake(PluginLifeCycle.ENABLE)
    fun onEnable() {
        onReload()
        PlayerEquipmentLoader.onEnable()
        NormalEquipmentLoader.onEnable()
    }
    @Awake(PluginLifeCycle.RELOAD)
    fun onReload() {
        slotConfig=Configuration.loadFromFile(File(AttributeSystem.dataDirectory.toFile(),"slot.yml"), Type.YAML)
        PlayerEquipmentLoader.onReload()
        NormalEquipmentLoader.onReload()
    }
}