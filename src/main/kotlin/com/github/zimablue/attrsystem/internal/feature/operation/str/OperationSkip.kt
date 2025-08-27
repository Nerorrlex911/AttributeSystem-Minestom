package com.github.zimablue.attrsystem.internal.feature.operation.str

import com.github.zimablue.attrsystem.api.operation.StringOperation

object OperationSkip : StringOperation("skip") {
    override fun operate(a: String, b: String): String {
        return a
    }
}