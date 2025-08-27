package com.github.zimablue.attrsystem.api.read.status

import com.github.zimablue.attrsystem.internal.core.read.BaseReadGroup

/**
 * Number status
 *
 * @constructor Create empty Number status
 * @property numberReader
 */
class StringStatus(numberReader: BaseReadGroup<String>) : Status<String>(numberReader) {
    override fun clone(): StringStatus {
        val attributeStatus = StringStatus(readGroup)
        this.forEach {
            attributeStatus.register(it.key, it.value)
        }
        return attributeStatus
    }
}
