package com.github.zimablue.attrsystem.internal.feature.operation.num

import com.github.zimablue.attrsystem.api.operation.NumberOperation
import kotlin.math.min

object OperationMin : NumberOperation("min", ">") {
    override fun operate(a: Number, b: Number): Number {
        return min(a.toDouble(), b.toDouble())
    }
}
