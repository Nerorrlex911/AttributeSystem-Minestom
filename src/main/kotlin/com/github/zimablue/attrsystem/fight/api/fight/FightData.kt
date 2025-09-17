package com.github.zimablue.attrsystem.fight.api.fight

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.api.AttrAPI.getAttrData
import com.github.zimablue.attrsystem.api.attribute.compound.AttributeDataCompound
import com.github.zimablue.attrsystem.api.operation.OperationElement
import com.github.zimablue.attrsystem.fight.api.event.FightDataHandleEvent
import com.github.zimablue.attrsystem.fight.api.fight.message.MessageData
import com.github.zimablue.attrsystem.internal.feature.calc.FormulaParser
import com.github.zimablue.attrsystem.internal.feature.compat.placeholder.AttributePlaceHolder
import com.github.zimablue.attrsystem.internal.feature.evalex.EvalEx
import com.github.zimablue.attrsystem.internal.manager.ASConfig.debug
import com.github.zimablue.attrsystem.internal.manager.ScriptManager
import com.github.zimablue.attrsystem.utils.parse
import com.github.zimablue.devoutserver.util.uncolored
import com.github.zimablue.pouplaceholder.PouPlaceholder
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.event.EventDispatcher
import net.minestom.server.event.trait.CancellableEvent
import taboolib.common.util.asList
import taboolib.common5.Coerce
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

/**
 * Fight data
 *
 * @constructor Create empty Fight data
 * @property attacker 攻击者
 * @property defender 防御者
 */
class FightData(attacker: LivingEntity?, defender: LivingEntity?) : ConcurrentHashMap<String, Any>() {
    constructor(attacker: LivingEntity?, defender: LivingEntity?, run: Consumer<FightData>) : this(attacker, defender) {
        run.accept(this)
    }

    var cache = DataCache(this)
        set(value) {
            field.variables.keys.forEach(this::remove)
            field = value
            value.data = this
            putAll(value.variables)
        }
    var attacker: LivingEntity? = null
        set(value) {
            field = value
            cache.attacker(value)
        }
    var defender: LivingEntity? = null
        set(value) {
            field = value
            cache.defender(value)
        }

    fun attackerData(attKey: String, params: List<String>) {
        cache.attackerData(attKey, params)
    }

    fun defenderData(attKey: String, params: List<String>) {
        cache.defenderData(attKey, params)
    }

    val attackerData: AttributeDataCompound
        get() = cache.attackerData ?: AttributeDataCompound()
    val defenderData: AttributeDataCompound
        get() = cache.defenderData ?: AttributeDataCompound()

    init {
        this.attacker = attacker
        this.defender = defender
    }

    var event: CancellableEvent? = null
        set(value) {
            field = value
            this["event"] = value!!
        }
        get() {
            return field ?: this["event"] as? CancellableEvent?
        }

    /** MessageType data */
    val messageData = MessageData()

    /** Damage sources */
    val damageSources = LinkedHashMap<String, OperationElement>()

    var damageTypes = LinkedHashMap<DamageType, FightData>()

    /** Has result */
    var hasResult = true

    /** Cal message */
    var calMessage = true

    /**
     * Cal result
     *
     * @return result
     */
    fun calResult(): Double {
        if (!hasResult) return 0.0
        var result = 0.0
        damageTypes.values.forEach {
            result += it.calResult()
        }
        damageSources.values.forEach {
            result = it.operate(result).toDouble()
        }
        return result
    }

    fun calMessage() {
        debug("fight-info-message")
        damageTypes.forEach { (type, fightData) ->
            fightData["result"] = fightData.calResult()
            if (attacker is Player && calMessage)
                type.attackMessage(attacker as Player, fightData, messageData.attackMessages.isEmpty())
                    ?.also { messageData.attackMessages.add(it) }
            if (defender is Player && calMessage) {
                type.defendMessage(defender as Player, fightData, messageData.defendMessages.isEmpty())
                    ?.also { messageData.defendMessages.add(it) }
            }
        }
    }


    constructor(fightData: FightData) : this(fightData.attacker, fightData.defender) {
        this.cache = fightData.cache
        putAll(fightData)
    }


    /**
     * Handle map
     *
     * @param map
     * @param K
     * @param V
     * @return
     */
    fun handleMap(map: Map<*, *>, log: Boolean = true): Map<String, Any> {
        val newMap = ConcurrentHashMap<String, Any>()
        map.forEach { (key, value) ->
            if (log)
                debug("      &e$key&5:")
            newMap[key.toString()] = handle(value ?: return@forEach, log)
        }
        return newMap
    }

    override fun toString(): String {
        return "FightData { Types: $damageTypes , DamageSources: $damageSources }"
    }

    /**
     * 解析Any
     *
     * 给脚本用的
     *
     * @param any 字符串/字符串集合/Map
     * @return 解析后的Any
     */
    fun handle(any: Any): Any {
        return handle(any, true)
    }

    /**
     * 解析Any
     *
     * @param any 字符串/字符串集合/Map
     * @return 解析后的Any
     */
    fun handle(any: Any, log: Boolean = true): Any {
        if (any is String) {
            return handleStr(any, log)
        }
        if (any is List<*>) {
            if (any.isEmpty()) return "[]"
            if (any[0] is Map<*, *>) {
                val mapList = Coerce.toListOf(any, Map::class.java)
                val newList = LinkedList<Map<*, *>>()
                mapList.forEach {
                    newList.add(handleMap(it))
                }
                return newList
            }
            return handleList(any.asList(), log)
        }
        if (any is Map<*, *>) {
            return handleMap(any)
        }
        return any
    }

    private fun String.attValue(entity: LivingEntity, data: AttributeDataCompound): String {
        val placeholder = substring(3)
        return AttributePlaceHolder.placeholder(placeholder, entity, data)
    }

    private fun String.placeholder(
        str: String,
        entity: LivingEntity,
        data: AttributeDataCompound,
        log: Boolean = true,
    ): String {
        val placeholder = str.substring(2)
        val value = if (placeholder.startsWith("as_")) placeholder.attValue(
            entity,
            data
        ) else PouPlaceholder.placeholderManager.replace(entity, "%${placeholder}%")
        if (log)
            debug(
                    "       &3{${str.uncolored()}} &7-> &9${
                        value.uncolored()
                    }"
                )

        return replace(
            "{$str}",
            value
        )
    }

    /**
     * Handle
     *
     * @param string 待解析字符串
     * @return 解析后的字符串
     */
    fun handleStr(string: String, log: Boolean = true): String {
        val event = FightDataHandleEvent(this, string)
        EventDispatcher.call(event)
        var formula = event.string
        val list = formula.parse('{', '}')
        for (str in list) {
            when {
                str.startsWith("a.") -> {
                    formula = formula.placeholder(str, attacker!!, attackerData, log)
                    continue
                }

                str.startsWith("d.") -> {
                    formula = formula.placeholder(str, defender!!, defenderData, log)
                }

                else -> {
                    val replacement = this[str] ?: continue
                    formula = formula.replace("{$str}", replacement.toString())
                    if (log)
                        debug(
                                "       &3{${str.uncolored()}} &7-> &9${
                                    replacement.toString().uncolored()
                                }"
                            )

                    continue
                }
            }
        }
        val value = formula.run {
            when {
                startsWith("!") -> substring(1)
                // File::Path::function::Aparms
                startsWith("File::") -> {
                    val new = substring(6)
                    val arguments = this@FightData
                    ScriptManager.pluginScriptManager.run(
                        new,
                        arguments,
                        this@FightData
                    ).toString()
                }
                startsWith("Formula::") -> {
                    val form = substring(9)
                    FormulaParser.calculate(form).toString()
                }
                startsWith("EvalEx::") -> {
                    val form = substring(8)
                    EvalEx.eval(form).toString()
                }
                else -> formula
            }
        }

        if (log) debug(
                "      &3${formula.uncolored()} &7-> &9${
                    value.uncolored()
                }"
            )
        return value
    }

    /**
     * 解析
     *
     * @param strings 待解析的字符串集
     * @return 解析后的字符串集
     */
    fun handleList(strings: Collection<String>, log: Boolean = true): List<String> {
        val list = ArrayList<String>()
        strings.forEach {
            list.add(handleStr(it, log))
        }
        return list
    }

    override fun clone(): FightData {
        return FightData(attacker, defender) {
            it.putAll(this)
        }
    }


}