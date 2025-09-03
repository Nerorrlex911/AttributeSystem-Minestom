package com.github.zimablue.attrsystem.internal.feature.calc

import com.github.zimablue.attrsystem.AttributeSystem
import java.util.*

object FormulaParser {

    private fun toCalcInfix(formula: String): ArrayList<Any> {
        val infix = arrayListOf<Any>()
        val num = StringBuilder()

        for (index in formula.indices) {
            val c = formula[index]
            if (!CalcOperator.isCalcOperator(c)) {
                if (c != ' ') num.append(c)
                continue
            }
            if (c == '+' || c == '-') {
                if (index == 0 || CalcOperator.isCalcOperatorExceptRightBracket(formula[index - 1])) {
                    num.append(c)
                    continue
                }
            }
            if (num.isNotEmpty()) {
                infix.add(num.toString().toDouble())
            }
            num.setLength(0)
            infix.add(CalcOperator.toCalcOperator(c))
        }
        if (num.isNotEmpty()) {
            infix.add(num.toString().toDouble())
        }
        return infix
    }

    private fun toCalcInfix(formula: String, args: MutableMap<String, Double>): ArrayList<Any> {
        val infix = arrayListOf<Any>()
        val num = StringBuilder()

        for (index in formula.indices) {
            val c = formula[index]
            if (!CalcOperator.isCalcOperator(c)) {
                if (c != ' ') num.append(c)
                continue
            }
            if (c == '+' || c == '-') {
                if (index == 0 || CalcOperator.isCalcOperatorExceptRightBracket(formula[index - 1])) {
                    num.append(c)
                    continue
                }
            }
            if (num.isNotEmpty()) {
                infix.add(args.computeIfAbsent(num.toString()) { it.toDouble() })
            }
            num.setLength(0)
            infix.add(CalcOperator.toCalcOperator(c))
        }
        if (num.isNotEmpty()) {
            infix.add(args.computeIfAbsent(num.toString()) { it.toDouble() })
        }
        return infix
    }

    private fun nextNotLessThan(stack: Stack<CalcOperator>, calcOperator: CalcOperator): Boolean {
        if (stack.isEmpty()) return false
        val peek = stack.peek()
        return peek != CalcOperator.LEFT_BRACKET && peek.priority >= calcOperator.priority
    }

    private fun toCalcSuffix(infix: List<Any>): Queue<Any> {
        val suffix: Queue<Any> = ArrayDeque()
        val operators = Stack<CalcOperator>()

        for (item in infix) {
            if (item is Double) {
                suffix.offer(item)
                continue
            }
            val calcOperator = item as CalcOperator
            if (calcOperator == CalcOperator.LEFT_BRACKET || calcOperator == CalcOperator.POWER) {
                operators.push(calcOperator)
            } else if (calcOperator == CalcOperator.RIGHT_BRACKET) {
                while (operators.isNotEmpty() && operators.peek() != CalcOperator.LEFT_BRACKET) {
                    suffix.offer(operators.pop())
                }
                if (operators.isNotEmpty()) operators.pop()
            } else {
                while (nextNotLessThan(operators, calcOperator)) {
                    suffix.offer(operators.pop())
                }
                operators.push(calcOperator)
            }
        }
        while (operators.isNotEmpty()) {
            suffix.offer(operators.pop())
        }
        return suffix
    }

    private fun calc(suffix: Queue<Any>): Double {
        val calcStack = Stack<Double>()
        while (suffix.isNotEmpty()) {
            val obj = suffix.poll()
            if (obj is Double) {
                calcStack.push(obj)
                continue
            }
            val calcOperator = obj as CalcOperator
            val a = if (calcStack.isEmpty()) 0.0 else calcStack.pop()
            val b = if (calcStack.isEmpty()) 0.0 else calcStack.pop()
            calcStack.push(calcOperator.calc(a, b))
        }
        return calcStack.pop()
    }

    fun calculate(formula: String): Double {
        return try {
            calc(toCalcSuffix(toCalcInfix(formula)))
        } catch (t: Throwable) {
            val params = hashMapOf<String, String>()
            params["{formula}"] = formula
            AttributeSystem.logger.error("Error calculating formula: $formula", t)
            0.0
        }
    }

    fun calculate(formula: String, args: MutableMap<String, Double>): Double {
        return try {
            calc(toCalcSuffix(toCalcInfix(formula, args)))
        } catch (t: Throwable) {
            val params = hashMapOf<String, String>()
            params["{formula}"] = formula
            AttributeSystem.logger.error("Error calculating formula: $formula", t)
            0.0
        }
    }
}
