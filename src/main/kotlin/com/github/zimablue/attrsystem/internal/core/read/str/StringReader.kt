package com.github.zimablue.attrsystem.internal.core.read.str

import com.github.zimablue.attrsystem.api.attribute.Attribute
import com.github.zimablue.attrsystem.api.read.status.Status
import com.github.zimablue.attrsystem.api.read.status.StringStatus
import com.github.zimablue.attrsystem.internal.core.read.BaseReadGroup
import net.minestom.server.entity.LivingEntity

/**
 * String reader
 *
 * 是AttributeSystem默认的读取格式实现，用于读取字符串属性
 *
 * @param key
 * @param matchers 捕获组
 * @param patternStrings 正则表达式
 * @param placeholders 占位符
 * @constructor
 */
class StringReader(
    key: String,
    matchers: Map<String, String>,
    patternStrings: List<String>,
    placeholders: Map<String, String>,
) : BaseReadGroup<String>(key, matchers, patternStrings, placeholders) {

    override fun read(string: String, attribute: Attribute, entity: LivingEntity?, slot: String?): StringStatus? {
        val attributeStatus = StringStatus(this)
        var temp = string
        attribute.names.forEach {
            if (temp.contains(it)) temp = temp.replaceFirst(it, "{name}")
        }
        patternList@ for ((pattern, matchers) in patterns) {
            val matcher = pattern.matcher(temp)
            if (!matcher.find()) continue
            matchers.forEach { usedMatcher ->
                val key = usedMatcher.key
                val valueStr = matcher.group(key)
                attributeStatus.operation(key, valueStr, usedMatcher.operation)
            }
            break@patternList
        }
        return attributeStatus
    }

    override fun readNBT(map: Map<String, Any>, attribute: Attribute): StringStatus {
        return StringStatus(this).apply {
            putAll(map.mapValues { it.value.toString() })
        }
    }

    override fun onPlaceholder(
        key: String,
        attribute: Attribute,
        status: Status<String>,
        entity: LivingEntity?,
    ): String? {
        return replacePlaceholder(key, status, entity)
    }
}