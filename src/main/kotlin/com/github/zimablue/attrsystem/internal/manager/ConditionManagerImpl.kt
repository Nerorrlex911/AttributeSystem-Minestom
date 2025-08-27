package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.api.condition.ConditionData
import com.github.zimablue.attrsystem.api.manager.ConditionManager
import com.github.zimablue.attrsystem.utils.clone
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.AwakePriority
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle

object ConditionManagerImpl: ConditionManager() {
    @Awake(PluginLifeCycle.ENABLE,AwakePriority.NORMAL)
    fun onEnable() {
        onReload()
    }
    @Awake(PluginLifeCycle.RELOAD,AwakePriority.NORMAL)
    fun onReload() {
        this.entries.filter { it.value.release }.forEach { this.remove(it.key) }
    }

    override fun matchConditions(text: String, slot: String?): Collection<ConditionData> =
        ArrayList<ConditionData>().apply {
            values.forEach { condition ->
                condition.parameters(text)?.let {
                    this += ConditionData(condition).push(HashMap(it).apply { put("slot", slot) })
                }
            }
        }

    override fun matchConditions(conditions: List<Map<String, Any>>, slot: String?): Collection<ConditionData> =
        ArrayList<ConditionData>().apply {
            conditions.forEach { map ->
                val key = map["key"].toString()
                val condition = get(key) ?: return@forEach
                val args = map.clone() as MutableMap<String, Any>
                slot?.let { args["slot"] = it }
                this += ConditionData(condition).push(args)
            }
        }
}