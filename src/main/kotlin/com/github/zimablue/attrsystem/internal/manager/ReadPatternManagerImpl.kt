package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.api.manager.ReadPatternManager
import com.github.zimablue.attrsystem.internal.core.read.BaseReadGroup
import com.github.zimablue.attrsystem.utils.getAllFiles
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.AwakePriority
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File

object ReadPatternManagerImpl: ReadPatternManager() {
    @Awake(PluginLifeCycle.ENABLE,AwakePriority.NORMAL)
    fun onEnable() {
        onReload()
    }
    @Awake(PluginLifeCycle.RELOAD,AwakePriority.NORMAL)
    fun onReload() {
        this.entries.filter { it.value.release }.forEach { (key, _) ->
            this.remove(key)?.also {
                AttributeSystem.logger.debug(
                    "Unregistering read group: {} with operations: {}",
                    key,
                    it.operations().map { operation -> operation.key }
                )
            }
        }
        getAllFiles(File(AttributeSystem.dataDirectory.toFile(),"reader"))
            .map{ Configuration.loadFromFile(it, Type.YAML)}
            .flatMap { it.getKeys(false).map { key -> it.getConfigurationSection(key)!! }}
            .forEach {
                val group = BaseReadGroup.deserialize(it)
                AttributeSystem.logger.debug(
                    "Registering read group: {} with operations: {}",
                    group.key.lowercase(),
                    group.operations().map { operation -> operation.key }
                )
                group.release = true
                group.register()
            }
    }
}