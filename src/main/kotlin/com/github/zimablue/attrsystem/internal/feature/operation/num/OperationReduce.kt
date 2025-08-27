package com.github.zimablue.attrsystem.internal.feature.operation.num

import com.github.zimablue.attrsystem.api.operation.NumberOperation

object OperationReduce : NumberOperation("reduce", "-") {
    override fun operate(a: Number, b: Number): Number {
        return a.toDouble() - b.toDouble()
    }
}
