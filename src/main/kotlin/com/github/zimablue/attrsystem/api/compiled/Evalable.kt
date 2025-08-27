package com.github.zimablue.attrsystem.api.compiled

import com.github.zimablue.attrsystem.api.attribute.compound.AttributeDataCompound
import net.minestom.server.entity.LivingEntity

/**
 * @className Evalable
 *
 * @author Glom
 * @date 2023/8/3 1:31 Copyright 2023 user. All rights reserved.
 */
fun interface Evalable {
    fun eval(entity: LivingEntity?): AttributeDataCompound
}