package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.AwakePriority
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import com.github.zimablue.devoutserver.util.createIfNotExists
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File
import java.util.regex.Pattern

object ASConfig {
    const val numberPattern: String = "(?<value>((?<=\\()(\\+|\\-)?(\\d+(?:(\\.\\d+))?)(?=\\)))|((\\+|\\-)?(\\d+(?:(\\.\\d+))?)))"

    lateinit var config: Configuration

    val ignores: List<String>
        get() = config.getStringList("options.read.ignores")

    var lineConditionPattern: Pattern = Pattern.compile("options.condition.line-condition.format")

    private val lineConditionFormat: String
        get() = config.getString("options.condition.line-condition.format") ?: "\\/(?<requirement>.*)"
    val lineConditionSeparator: String
        get() = config.getString("options.condition.line-condition.separator") ?: ","

    @Awake(PluginLifeCycle.LOAD)
    fun onLoad() {
        createIfNotExists("reader", "number/default.yml", "number/percent.yml", "string/string.yml")
        createIfNotExists(
            "attributes",
            "Example.yml"
        )
        createIfNotExists(
            "scripts",
            "conditions/slot.js",
            "conditions/attribute.js",
        )
    }
    @Awake(PluginLifeCycle.ENABLE,AwakePriority.LOW)
    fun onEnable() {
        config = Configuration.loadFromFile(File(AttributeSystem.dataDirectory.toFile(),"config.yml"),Type.YAML)
        lineConditionPattern = Pattern.compile(lineConditionFormat)
    }

    val debug: Boolean
        get() = config.getBoolean("options.debug")
    @JvmStatic
    fun debug(debug: () -> Unit) {
        if (this.debug) {
            debug.invoke()
        }
    }
    @JvmStatic
    fun debug(debug: String) {
        if (this.debug) {
            AttributeSystem.logger.debug(debug)
        }
    }
}