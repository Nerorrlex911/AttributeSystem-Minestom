package com.github.zimablue.attrsystem.fight.api.event

import com.github.zimablue.attrsystem.fight.api.fight.FightData
import com.github.zimablue.attrsystem.fight.api.fight.mechanic.Mechanic
import net.minestom.server.event.trait.CancellableEvent


class MechanicRunEvent {
    /**
     * 运行机制前
     *
     * @property mechanic 机制
     * @property fightData 战斗数据
     * @property context 上下文(战斗组中这个机制的参数)
     * @property damageType 伤害类型
     * @property result 机制运行结果
     */
    class Pre(
        val mechanic: Mechanic,
        val fightData: FightData,
        val context: Map<String, Any>,
        val damageType: com.github.zimablue.attrsystem.fight.api.fight.DamageType,
        var result: Any?,
    ) : CancellableEvent {
        private var isCancelled = false
        override fun isCancelled(): Boolean = isCancelled
        override fun setCancelled(cancel: Boolean) {
            isCancelled = cancel
        }
    }

    /**
     * 运行机制后
     *
     * @property mechanic 机制
     * @property fightData 战斗数据
     * @property context 上下文(战斗组中这个机制的参数)
     * @property damageType 伤害类型
     * @property result 机制运行结果
     */

    class Post(
        val mechanic: Mechanic,
        val fightData: FightData,
        val context: Map<String, Any>,
        val damageType: com.github.zimablue.attrsystem.fight.api.fight.DamageType,
        var result: Any?,
    ) : CancellableEvent {
        private var isCancelled = false
        override fun isCancelled(): Boolean = isCancelled
        override fun setCancelled(cancel: Boolean) {
            isCancelled = cancel
        }
    }

}
