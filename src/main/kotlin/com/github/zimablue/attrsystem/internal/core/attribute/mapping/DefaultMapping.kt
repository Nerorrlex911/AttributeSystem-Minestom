package com.github.zimablue.attrsystem.internal.core.attribute.mapping

import com.github.zimablue.attrsystem.AttributeSystem.readManager
import com.github.zimablue.attrsystem.api.Mapping
import com.github.zimablue.attrsystem.api.compiled.CompiledData
import com.github.zimablue.attrsystem.api.read.status.Status
import com.github.zimablue.attrsystem.utils.replaceThenCalc
import net.minestom.server.entity.LivingEntity

/**
 * @className DefaultMapping
 *
 * @author Glom
 * @date 2023/8/5 15:35 Copyright 2023 user. All rights reserved.
 */
class DefaultMapping(val map: Map<String, Any>) : Mapping() {
    override fun mapping(status: Status<*>, entity: LivingEntity?): CompiledData? {
        attribute ?: return null
        val replacement =
            (status as? Status<*>)?.readGroup?.run {
                placeholderKeys.associate { "<${it}>" to placeholder(it, attribute!!, status, entity).toString() }
            } ?: return null
        val mapping = map.replaceThenCalc(replacement, entity) as Map<String, Any>
        return readManager.readMap(mapping, entity)
    }
}