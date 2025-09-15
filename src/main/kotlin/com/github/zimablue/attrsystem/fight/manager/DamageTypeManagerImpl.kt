package com.github.zimablue.attrsystem.fight.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.fight.api.fight.DamageType
import com.github.zimablue.attrsystem.fight.api.manager.DamageTypeManager
import com.github.zimablue.attrsystem.utils.getAllFiles
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import taboolib.module.configuration.Configuration

object DamageTypeManagerImpl : DamageTypeManager() {

    @Awake(PluginLifeCycle.ENABLE)
    fun onEnable() {
        onReload()
    }

    @Awake(PluginLifeCycle.RELOAD)
    fun onReload() {
        clear()
        getAllFiles(AttributeSystem.dataDirectory.resolve("damage_type").toFile()).forEach {
            val conf = Configuration.loadFromFile(it)
            conf.getKeys(false).map { key ->
                val section = conf.getConfigurationSection(key) ?: return@map
                DamageType.deserialize(section).register()
            }
        }
    }
}