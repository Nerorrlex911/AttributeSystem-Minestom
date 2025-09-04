package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.utils.createIfNotExists
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.AwakePriority
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle

import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File
import java.util.regex.Pattern

object ASConfig {
    const val numberPattern: String = "(?<value>((?<=\\()(\\+|\\-)?(\\d+(?:(\\.\\d+))?)(?=\\)))|((\\+|\\-)?(\\d+(?:(\\.\\d+))?)))"

    lateinit var config: Configuration
    lateinit var lang: Configuration

    val loreEnable: Boolean
        get() = config.getBoolean("options.lore-enable",false)

    val ignores: List<String>
        get() = config.getStringList("options.read.ignores")

    var lineConditionPattern: Pattern = Pattern.compile("options.condition.line-condition.format")

    private val lineConditionFormat: String
        get() = config.getString("options.condition.line-condition.format") ?: "\\/(?<requirement>.*)"
    val lineConditionSeparator: String
        get() = config.getString("options.condition.line-condition.separator") ?: ","


    val statsTitle: String
        get() = lang.getString("stats-title")!!
    val statsStatus
        get() = lang.getString("stats-status")!!
    val statusAttributeFormat
        get() = lang.getString("stats-attribute-format")!!

    val statusNone
        get() = lang.getString("stats-status-none")!!

    val statusValue
        get() = lang.getString("stats-status-value")!!

    val statusPlaceholder
        get() = lang.getString("stats-status-placeholder")!!

    val statusPlaceholderValue
        get() = lang.getString("stats-status-placeholder-value")!!

    val statsEnd: String
        get() = lang.getString("stats-end")!!

    @Awake(PluginLifeCycle.LOAD)
    fun onLoad() {
        AttributeSystem.savePackagedResource("config.yml")
        AttributeSystem.savePackagedResource("lang.yml")
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
        config = Configuration.loadFromFile(AttributeSystem.dataDirectory.resolve("config.yml").toFile(),Type.YAML)
        lang = Configuration.loadFromFile(AttributeSystem.dataDirectory.resolve("lang.yml").toFile(),Type.YAML)
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