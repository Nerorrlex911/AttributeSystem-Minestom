package com.github.zimablue.attrsystem.api.condition

/**
 * @className ConditionReader
 *
 * 从字符串中读取条件的参数
 *
 * @author Glom
 * @date 2023/8/1 23:55 Copyright 2022 user.
 */
fun interface ConditionReader {

    /**
     * 从字符串中读取条件的参数
     *
     * @param text String 文本
     * @return Map<String, Any> 参数
     */
    fun parameters(
        text: String,
    ): Map<String, Any>?

}