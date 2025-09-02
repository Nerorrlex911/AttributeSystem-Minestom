package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.AttributeSystem.conditionManager
import com.github.zimablue.attrsystem.AttributeSystem.logger
import com.github.zimablue.attrsystem.api.condition.BaseCondition
import com.github.zimablue.attrsystem.internal.manager.ASConfig.debug
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.AwakePriority
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import com.github.zimablue.devoutserver.plugin.script.PluginScriptManager
import net.minestom.server.entity.LivingEntity
import taboolib.common5.cbool
import taboolib.module.configuration.util.asMap
import java.io.File
import java.util.regex.Pattern
import javax.script.ScriptContext.ENGINE_SCOPE

object ScriptManager {
    val pluginScriptManager = PluginScriptManager(AttributeSystem,File(AttributeSystem.dataDirectory.toFile(),"scripts"))

    private val conditionKeys = mutableListOf<String>()

    @Awake(PluginLifeCycle.ENABLE,AwakePriority.HIGH)
    fun onEnable() {
        onReload()
    }

    @Awake(PluginLifeCycle.RELOAD,AwakePriority.HIGH)
    fun onReload() {
        conditionKeys.forEach {
            conditionManager.remove(it)
            debug { logger.info("Condition $it unregistered") }
        }
        conditionKeys.clear()

        pluginScriptManager.compiledScripts.forEach { (name, script) ->
            val vars = script.scriptEngine.getBindings(ENGINE_SCOPE)
            val key = vars["key"]?.toString() ?: return@forEach
            debug{ logger.info("Loading Condition: $key") }
            val names = vars["names"].asMap().values.map { Pattern.compile(it.toString()) }
            if(!script.isFunction("condition")) {
                logger.error("function condition(entity,parameters) of Condition $key not found")
                return@forEach
            }
            if(script.isFunction("parameters")) {
                object : BaseCondition(key) {
                    override fun parameters(text: String): Map<String, Any>? {
                        val matcher = names.map { it.matcher(text) }.firstOrNull { it.find() } ?: return null
                        return pluginScriptManager.run(name,"parameters", null, arrayOf(matcher, text)) as? Map<String, Any>?
                    }
                    override fun condition(entity: LivingEntity?, parameters: Map<String, Any>): Boolean {
                        return pluginScriptManager.run(name,"condition", null, arrayOf(entity, parameters)).cbool
                    }
                }.register()
            } else {
                object : BaseCondition(key) {
                    override fun condition(entity: LivingEntity?, parameters: Map<String, Any>): Boolean {
                        return pluginScriptManager.run(name,"condition", null, arrayOf(entity, parameters)).cbool
                    }
                }.register()
            }

            conditionKeys.add(key)
            debug { logger.info("Condition $key registered by script ${script.name}") }
        }
    }

}