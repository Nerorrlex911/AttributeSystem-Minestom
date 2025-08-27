package com.github.zimablue.attrsystem.internal.feature.operation.num

import com.github.zimablue.attrsystem.api.operation.NumberOperation
import kotlin.math.max


object OperationMax : NumberOperation("max", "<") {
    override fun operate(a: Number, b: Number): Number {
        return max(a.toDouble(), b.toDouble())
    }
}
