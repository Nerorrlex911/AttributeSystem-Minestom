package com.github.zimablue.attrsystem.api.operation

/**
 * @className OperationElement
 *
 * @author Glom
 * @date 2022/7/19 12:37 Copyright 2022 user.
 */
data class OperationElement(val numberOperation: NumberOperation, val number: Number) {
    /**
     * 做运算
     *
     * @param other 其他数字
     * @return 运算结果
     */
    fun operate(other: Number): Number {
        return numberOperation.operate(other, number)
    }

    override fun toString(): String {
        return "Operation { ${numberOperation.key} $number }"
    }
}