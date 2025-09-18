package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.internal.feature.vanilla.VanillaAttribute
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import com.github.zimablue.devoutserver.util.map.KeyMap
import net.minestom.server.entity.LivingEntity
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type

object VanillaAttributeManager : KeyMap<String,VanillaAttribute>(){
    lateinit var vanilla: Configuration

    fun update(entity: LivingEntity) {
        VanillaAttributeManager.forEach { _, attr ->
            attr.update(entity)
        }
    }

    @Awake(PluginLifeCycle.LOAD)
    fun onLoad() {
        AttributeSystem.savePackagedResource("vanilla.yml")
    }
    @Awake(PluginLifeCycle.ENABLE)
    fun onEnable() {
        onReload()
    }
    @Awake(PluginLifeCycle.RELOAD)
    fun onReload() {
        vanilla = Configuration.loadFromFile(
            AttributeSystem.dataDirectory.resolve("vanilla.yml").toFile(),
            Type.YAML
        )
        clear()
        for (key in vanilla.getKeys(false)) {
            try {
                val attribute = VanillaAttribute(key)
                if (attribute.enable) {
                    register(attribute)
                }
            } catch (e: Exception) {
                println("§c[AttributeSystem] Error loading vanilla attribute: $key")
                e.printStackTrace()
            }
        }
    }
}