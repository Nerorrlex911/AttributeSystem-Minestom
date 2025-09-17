package com.github.zimablue.attrsystem.internal.feature.evalex

import com.ezylang.evalex.Expression
import com.ezylang.evalex.config.ExpressionConfiguration


object EvalEx {
    var configuration: ExpressionConfiguration = ExpressionConfiguration.builder()
        .arraysAllowed(false)
        .build()

    fun eval(formula: String): Any {
        return Expression(formula, configuration).evaluate().value
    }
    fun eval(formula: String,map: Map<String,Any>): Any {
        return Expression(formula,configuration).withValues(map).evaluate()
    }
}