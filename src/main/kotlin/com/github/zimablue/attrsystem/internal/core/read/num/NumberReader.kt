package com.github.zimablue.attrsystem.internal.core.read.num

import com.github.zimablue.attrsystem.api.attribute.Attribute
import com.github.zimablue.attrsystem.api.read.status.NumberStatus
import com.github.zimablue.attrsystem.api.read.status.Status
import com.github.zimablue.attrsystem.internal.core.read.BaseReadGroup
import com.github.zimablue.attrsystem.internal.manager.ASConfig.numberPattern
import net.minestom.server.entity.LivingEntity
import taboolib.common5.Coerce
import taboolib.common5.cdouble


/**
 * Number reader 数字读取组
 *
 * 是AttributeSystem默认的读取格式实现，用于读取数字属性
 *
 * @param key
 * @param matchers 捕获组
 * @param patternStrings 正则表达式
 * @param placeholders 占位符
 * @constructor
 */
open class NumberReader(
    key: String,
    matchers: Map<String, String>,
    patternStrings: List<String>,
    placeholders: Map<String, String>,
) : BaseReadGroup<Double>(key, matchers, patternStrings, placeholders, numberPattern) {

    override fun read(string: String, attribute: Attribute, entity: LivingEntity?, slot: String?): NumberStatus? {
        
        val status = NumberStatus(this)
        var temp = string
        attribute.names.forEach {
            if (temp.contains(it)) temp = temp.replace(it, "{name}")
        }
        patternList@ for ((pattern, matchers) in patterns) {
            val matcher = pattern.matcher(temp)
            if (!matcher.find()) continue
            matchers.forEach { usedMatcher ->
                val key = usedMatcher.key
                val valueStr = matcher.group(key)
                Coerce.asDouble(valueStr).ifPresent {
                    status.operation(key, it, usedMatcher.operation)
                }
            }
            break@patternList
        }
        return status
    }

    override fun readNBT(map: Map<String, Any>, attribute: Attribute): NumberStatus {
        return NumberStatus(this).apply {
            putAll(map.mapValues { it.value.cdouble })
        }
    }

    override fun onPlaceholder(
        key: String,
        attribute: Attribute,
        status: Status<Double>,
        entity: LivingEntity?,
    ): Double? {
        return replacePlaceholder(key, status, entity)?.cdouble
    }


}