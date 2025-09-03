package com.github.zimablue.attrsystem.internal.feature.calc

import kotlin.math.pow

enum class CalcOperator(
    private val symbol: Char,
    val priority: Int,
    val calc: (Double, Double) -> Double
) {
    PLUS('+', 1, { a, b -> b + a }),
    MINUS('-', 1, { a, b -> b - a }),
    MULTIPLY('*', 2, { a, b -> a * b }),
    DIVIDE('/', 2, { a, b -> b / a }),
    REMAIN('%', 2, { a, b -> b % a }),
    RANDOM('~', 2, { a, b ->
        val min = minOf(a, b)
        val max = maxOf(a, b)
        kotlin.random.Random.nextDouble(min, max)
    }),
    POWER('^', 3, { a, b -> b.pow(a) }),
    LEFT_BRACKET('(', 3, { _, _ -> 0.0 }),
    RIGHT_BRACKET(')', 3, { _, _ -> 0.0 });

    override fun toString(): String = symbol.toString()

    companion object {
        private val bySymbol = entries.associateBy { it.symbol }
        private val symbolsExceptRightBracket = entries
            .filterNot { it == RIGHT_BRACKET }
            .map { it.symbol }
            .toSet()

        fun isCalcOperator(c: Char): Boolean = bySymbol.containsKey(c)

        fun isCalcOperatorExceptRightBracket(c: Char): Boolean =
            symbolsExceptRightBracket.contains(c)

        fun toCalcOperator(c: Char): CalcOperator =
            bySymbol[c] ?: error("No such Operator $c")
    }
}
