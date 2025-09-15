package com.github.zimablue.attrsystem.fight.api.event

import com.github.zimablue.attrsystem.fight.api.fight.DamageType
import com.github.zimablue.attrsystem.fight.api.fight.FightData
import net.minestom.server.event.trait.CancellableEvent


class DamageTypeRunEvent {
    /**
     * 运行此伤害类型下的判断enable以及机制前
     *
     * @property type 伤害类型
     * @property fightData 战斗数据
     * @property enable 是否启用
     */
    class Pre(
        val type: DamageType,
        val fightData: FightData,
        var enable: Boolean,
    ) : CancellableEvent {
        private var isCancelled = false
        override fun isCancelled(): Boolean = isCancelled
        override fun setCancelled(cancel: Boolean) {
            isCancelled = cancel
        }
    }

    /**
     * 运行此伤害类型下的判断enable以及机制后
     *
     * @property type 伤害类型
     * @property fightData 战斗数据
     */
    class Post(
        val type: DamageType,
        val fightData: FightData,
    ) : CancellableEvent {
        private var isCancelled = false
        override fun isCancelled(): Boolean = isCancelled
        override fun setCancelled(cancel: Boolean) {
            isCancelled = cancel
        }
    }

}
