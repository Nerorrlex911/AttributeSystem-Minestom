package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.AttributeSystem.langManager
import com.github.zimablue.attrsystem.utils.console
import com.github.zimablue.attrsystem.utils.createIfNotExists
import com.github.zimablue.attrsystem.utils.langInfo
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.AwakePriority
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import com.github.zimablue.devoutserver.util.map.BaseMap
import net.minestom.server.MinecraftServer
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.util.regex.Pattern

object ASConfig {
    const val numberPattern: String = "(?<value>((?<=\\()(\\+|\\-)?(\\d+(?:(\\.\\d+))?)(?=\\)))|((\\+|\\-)?(\\d+(?:(\\.\\d+))?)))"

    lateinit var config: Configuration
    lateinit var options: Configuration

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
        get() = langManager.asLangText(console(),"stats-title")[0]
    val statsStatus
        get() = langManager.asLangText(console(), "stats-status")[0]
    val statusAttributeFormat
        get() = langManager.asLangText(console(), "stats-attribute-format")[0]

    val statusNone
        get() = langManager.asLangText(console(), "stats-status-none")[0]

    val statusValue
        get() = langManager.asLangText(console(), "stats-status-value")[0]

    val statusPlaceholder
        get() = langManager.asLangText(console(), "stats-status-placeholder")[0]

    val statusPlaceholderValue
        get() = langManager.asLangText(console(), "stats-status-placeholder-value")[0]

    val statsEnd: String
        get() = langManager.asLangText(console(), "stats-end")[0]

    // FightSystem configs

    lateinit var message: Configuration

    val attackFightKeyMap = BaseMap<String, String>()

    val defaultAttackerName: String
        get() = message.getString("fight-message.default-name.attacker") ?: "大自然"
    val defaultDefenderName: String
        get() = message.getString("fight-message.default-name.defender") ?: "未知"

    val defaultAttackMessageType: String
        get() = (message.getString("options.default.attack") ?: "HOLO").uppercase()

    val defaultDefendMessageType: String
        get() = (message.getString("options.default.defend") ?: "CHAT").uppercase()

    val defaultRegainHolo: Boolean
        get() = message.getBoolean("options.default.health-regain-holo")

    @Awake(PluginLifeCycle.LOAD,AwakePriority.LOWEST)
    fun onLoad() {
        AttributeSystem.savePackagedResource("config.yml")
        AttributeSystem.savePackagedResource("slot.yml")
        AttributeSystem.savePackagedResource("options.yml")

        AttributeSystem.savePackagedResource("message.yml")

        createIfNotExists("reader", "number/default.yml", "number/percent.yml", "string/string.yml")
        createIfNotExists(
            "attributes",
            "Example.yml",
            "其他.yml",
            "readme.txt",
            "物理.yml",
            "特殊.yml"
        )

        createIfNotExists("fight_group", "default.yml", "skapi.yml", "mythic_skill.yml", "damage_event.yml")
        createIfNotExists("damage_type", "magic.yml", "physical.yml", "real.yml")
    }
    @Awake(PluginLifeCycle.ENABLE,AwakePriority.LOWEST)
    fun onEnable() {
        onReload()
        val parentNode = config.getString("parent-node","global")
        val nodePriority = config.getInt("node-priority",3)
        AttributeSystem.asEventNode.setPriority(nodePriority)
        if(parentNode=="global") {
            MinecraftServer.getGlobalEventHandler().addChild(AttributeSystem.asEventNode)
        } else {
            val node = MinecraftServer.getGlobalEventHandler().findChildren(parentNode)[0]
            node?.addChild(AttributeSystem.asEventNode)
        }
    }
    @Awake(PluginLifeCycle.RELOAD,AwakePriority.LOWEST)
    fun onReload() {
        config = Configuration.loadFromFile(AttributeSystem.dataDirectory.resolve("config.yml").toFile(),Type.YAML)
        options = Configuration.loadFromFile(AttributeSystem.dataDirectory.resolve("options.yml").toFile(),Type.YAML)
        message = Configuration.loadFromFile(AttributeSystem.dataDirectory.resolve("message.yml").toFile(),Type.YAML)
        lineConditionPattern = Pattern.compile(lineConditionFormat)
        config.getStringList("options.fight.attack-fight").onEach {
            val array = it.split("::")
            if (array.size != 2) return@onEach
            attackFightKeyMap[array[0]] = array[1]
        }
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
            AttributeSystem.logger.info(debug)
        }
    }
    fun debugLang(debug: String,vararg args: String) {
        if (this.debug) {
            AttributeSystem.logger.langInfo(debug,*args)
        }
    }
}