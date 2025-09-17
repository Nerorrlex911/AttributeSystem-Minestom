package com.github.zimablue.attrsystem.fight.api.fight.mechanic

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.fight.api.event.MechanicRunEvent
import com.github.zimablue.attrsystem.fight.api.fight.DamageType
import com.github.zimablue.attrsystem.fight.api.fight.FightData
import com.github.zimablue.devoutserver.util.map.component.Registrable
import net.minestom.server.event.EventDispatcher


/**
 * Mechanic
 *
 * @constructor Create empty Mechanic
 * @property key 键
 */
abstract class Mechanic(override val key: String) :
    Registrable<String> {

    /**
     * Exec
     *
     * @param fightData 战斗数据
     * @param context 上下文 （机制在战斗组中的参数）
     * @param damageType 伤害类型
     * @return 返回值
     */
    abstract fun exec(
        fightData: FightData,
        context: Map<String, Any>,
        damageType: DamageType,
    ): Any?

    /** 是否在重载时删除 */
    var release = false

    /**
     * Run
     *
     * 运行机制
     *
     * @param fightData 战斗数据
     * @param context 上下文 （机制在战斗组中的参数）
     * @param damageType 伤害类型
     * @return 返回值
     */
    fun run(
        fightData: FightData,
        context: Map<String, Any>,
        damageType: DamageType,
    ): Any? {
        val before =
            MechanicRunEvent.Pre(this, fightData, context, damageType, null)
        EventDispatcher.call(before)
        val result = exec(fightData, context, damageType)
        if (before.isCancelled) return null
        val post =
            MechanicRunEvent.Post(this, fightData, context, damageType, result)
        EventDispatcher.call(post)
        if (post.isCancelled) return null
        return post.result
    }

    final override fun register() {
        AttributeSystem.mechanicManager.register(this)
    }

}
