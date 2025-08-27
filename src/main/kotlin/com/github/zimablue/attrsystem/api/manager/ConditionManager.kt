package com.github.zimablue.attrsystem.api.manager

import com.github.zimablue.attrsystem.api.condition.BaseCondition
import com.github.zimablue.attrsystem.api.condition.ConditionData
import com.github.zimablue.devoutserver.util.map.LowerKeyMap

abstract class ConditionManager : LowerKeyMap<BaseCondition>() {
    /**
     * 匹配条件
     *
     * @param text String 字符串
     * @param slot String? 槽位
     * @return Collection<ConditionData> 条件数据
     */
    abstract fun matchConditions(text: String, slot: String?): Collection<ConditionData>

    /**
     * 匹配条件
     *
     * conditions例如:
     * ```
     *    conditions:
     *     - key: food
     *       value: 15
     *     - key: attribute
     *       name: 生命值
     *       value: 10
     *
     * ```
     *
     * @param conditions List<Map<String, Any>> 条件列表
     * @param slot String? 槽位
     * @return Collection<ConditionData> 条件数据
     */
    abstract fun matchConditions(conditions: List<Map<String, Any>>, slot: String?): Collection<ConditionData>
}