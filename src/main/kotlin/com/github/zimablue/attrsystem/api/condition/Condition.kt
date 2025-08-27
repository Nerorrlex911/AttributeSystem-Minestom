package com.github.zimablue.attrsystem.api.condition

import net.minestom.server.entity.LivingEntity

/**
 * @className Condition
 *
 * @author Glom
 * @date 2022/7/18 23:55 Copyright 2022 user. 
 */
fun interface Condition {
    /**
     * 验证条件
     *
     * @param entity 实体 (可为null)
     * @param parameters 参数
     * @return 是否满足条件
     */
    fun condition(
        entity: LivingEntity?,
        parameters: Map<String, Any>,
    ): Boolean

}