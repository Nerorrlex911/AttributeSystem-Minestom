package com.github.zimablue.attrsystem.api

import com.github.zimablue.attrsystem.api.attribute.Attribute
import com.github.zimablue.attrsystem.api.compiled.CompiledData
import com.github.zimablue.attrsystem.api.read.status.Status
import net.minestom.server.entity.LivingEntity

/**
 * @className Mapping
 *
 * @author Glom
 * @date 2023/8/5 14:57 Copyright 2023 user. All rights reserved.
 */
abstract class Mapping {
    var attribute: Attribute? = null
    abstract fun mapping(status: Status<*>, entity: LivingEntity?): CompiledData?
}