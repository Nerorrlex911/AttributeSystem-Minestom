package com.github.zimablue.attrsystem.internal.feature.attribute

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.api.AttrAPI.read
import com.github.zimablue.attrsystem.api.compiled.CompiledAttrDataCompound
import com.github.zimablue.attrsystem.api.compiled.sub.ComplexCompiledData
import com.github.zimablue.attrsystem.internal.manager.ASConfig
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import taboolib.common.util.asList
import taboolib.module.configuration.util.asMap

object BaseAttributeEntity {

    val type
        get() = ASConfig.options.getString("base-attribute-player.type")?.lowercase() ?: "strings"
    val attrData
        get() = ASConfig.options["base-attribute-player.attributes"]
    val conditions
        get() = ASConfig.options["base-attribute-player.conditions"]

    private const val KEY = "BASIC-ATTRIBUTE"

    var baseData: ComplexCompiledData = ComplexCompiledData()


    @Awake(PluginLifeCycle.ENABLE)
    fun onEnable() {
        onReload()
    }

    @Awake(PluginLifeCycle.RELOAD)
    fun onReload() {
        val base = when (type) {
            "nbt" -> {
                val attrData = attrData.asMap().entries.associate { it.key to it.value!! }.toMutableMap()
                val conditions = conditions as? List<Any>? ?: emptyList()
                AttributeSystem.readManager.readMap(attrData, conditions)
            }

            else -> attrData?.asList()?.read()
        }
        baseData.base = base
    }

    fun CompiledAttrDataCompound.baseEntity(): CompiledAttrDataCompound {
        this[KEY] = baseData
        return this
    }
}