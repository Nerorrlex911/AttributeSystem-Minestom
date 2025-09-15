package com.github.zimablue.attrsystem.fight.api.event

import com.github.zimablue.attrsystem.fight.api.fight.FightData
import net.minestom.server.event.Event
import net.minestom.server.event.trait.CancellableEvent

class FightEvent {
    /**
     * 攻击前事件 机制组还没有开始运行
     *
     * @property key 战斗组键
     * @property fightData 战斗数据
     */

    class Pre(val key: String, val fightData: FightData) : CancellableEvent {
        private var isCancelled = false
        override fun isCancelled(): Boolean = isCancelled
        override fun setCancelled(cancel: Boolean) {
            isCancelled = cancel
        }
        val attacker = fightData.attacker
        val defender = fightData.defender
        val hasAttacker = attacker != null
        val hasDefender = defender != null
    }

    /**
     * 攻击中事件 机制组运行完毕，未计算出总伤害
     *
     * @property key 战斗组键
     * @property fightData 战斗数据
     */
    class Process(val key: String, val fightData: FightData) : CancellableEvent {
        private var isCancelled = false
        override fun isCancelled(): Boolean = isCancelled
        override fun setCancelled(cancel: Boolean) {
            isCancelled = cancel
        }
        val defender = fightData.defender
        val attacker = fightData.attacker
        val hasAttacker = attacker != null
        val hasDefender = defender != null
    }

    /**
     * 攻击后事件 机制组运行完毕，已计算出伤害
     *
     * @property key 战斗组键
     * @property fightData 战斗数据
     */
    class Post(val key: String, val fightData: FightData) : CancellableEvent {
        private var isCancelled = false
        override fun isCancelled(): Boolean = isCancelled
        override fun setCancelled(cancel: Boolean) {
            isCancelled = cancel
        }
        val defender = fightData.defender
        val attacker = fightData.attacker
        val hasAttacker = attacker != null
        val hasDefender = defender != null
    }
}
