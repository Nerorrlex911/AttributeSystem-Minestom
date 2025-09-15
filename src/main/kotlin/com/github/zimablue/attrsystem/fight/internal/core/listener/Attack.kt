package com.github.zimablue.attrsystem.fight.internal.core.listener

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.fight.api.FightAPI
import com.github.zimablue.attrsystem.fight.api.FightAPI.intoFighting
import com.github.zimablue.attrsystem.fight.api.fight.FightData
import com.github.zimablue.attrsystem.fight.manager.ProjectileRealizer
import com.github.zimablue.attrsystem.fight.manager.ProjectileRealizer.cache
import com.github.zimablue.attrsystem.fight.manager.ProjectileRealizer.charged
import com.github.zimablue.attrsystem.internal.manager.ASConfig.attackFightKeyMap
import com.github.zimablue.attrsystem.internal.manager.ASConfig.config
import com.github.zimablue.attrsystem.internal.manager.ASConfig.debug
import com.github.zimablue.attrsystem.utils.isAlive
import com.github.zimablue.devoutserver.feature.luckperms.LuckPerms.hasPermission
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.entity.damage.DamageType
import net.minestom.server.entity.damage.EntityProjectileDamage
import net.minestom.server.event.entity.EntityDamageEvent
import taboolib.common5.cfloat

object Attack {
    val isFightEnable
        get() = config.getBoolean("options.fight.enable", true)
    val eveFightCal
        get() = config.getBoolean("options.fight.eve-fight-cal",false)
    val defaultFightGroup: String
        get() = config.getString("options.fight.default","attack-damage")!!

    @Awake(PluginLifeCycle.ENABLE)
    fun onEnable() {
        AttributeSystem.asEventNode
            .addListener(EntityDamageEvent::class.java) { event ->
                onAttack(event)
                onDamage(event)
            }
    }

    fun onAttack(event: EntityDamageEvent) {
        if (!isFightEnable) return
        //如果攻击原因不是 ENTITY_ATTACK 和 PROJECTILE 则跳过计算
        val isAttack = event.damage.type == DamageType.MOB_ATTACK || event.damage.type == DamageType.PLAYER_ATTACK
        val isProjectile = event.damage is EntityProjectileDamage
        if (!isAttack && !isProjectile) return


        val attacker = event.damage.attacker as? LivingEntity ?: return
        val defender = event.entity
        //判断是否都是存活实体                                防御方为盔甲架则跳过计算
        if (!attacker.isAlive() || !defender.isAlive() || defender.entityType == EntityType.ARMOR_STAND) return
        defender as LivingEntity

        //是否是EVE (非玩家 打 非玩家)                       如果关闭EVE计算则跳过计算
        if (attacker !is Player && defender !is Player && !eveFightCal) return

        //事件取消则跳过计算
        if (event.isCancelled) return

        //如果不是原版弓/弩攻击 则跳过计算, attacker为绝对攻击者
        if (isProjectile && attacker.charged() == null) return

        if (FightAPI.filters.any {
                it(attacker, defender)
            }) return
        //原伤害
        val originDamage = event.damage.amount

        debug("Handling Damage Event...")

        //处理战斗组id
        val fightKey =
            if(attacker is Player)
                attackFightKeyMap.filterKeys { attacker.hasPermission(it) }.values.firstOrNull() ?: defaultFightGroup
            else defaultFightGroup

        debug ("FightKey: $fightKey")
        val cacheData = event.damage.source?.cache()
        val data = FightData(attacker, defender).also {
            if (ProjectileRealizer.defaultEnable && cacheData != null){
                debug ("Use cache")
                it.cache.setData(cacheData)
            }
        }

        //运行战斗组并返回结果
        val result = FightAPI.runFight(fightKey, data.also {
            //往里塞参数
            it["origin"] = originDamage
            it["event"] = event
            it["projectile"] = isProjectile.toString()
            it["fightData"] = it
        }, damage = false)


        //结果小于等于零，代表MISS 未命中
        if (result <= 0.0) {
            debug ("Cancelled because Result <= 0")
            event.isCancelled = true
            return
        }
        // 设置伤害
        event.damage.amount = result.cfloat

    }

    fun onDamage(event: EntityDamageEvent) {
        val attacker: LivingEntity? = event.damage.attacker as? LivingEntity
        val defender = event.entity ?: return
        if (!defender.isAlive()) return
        val cause = event.damage.type
        val key = "damage-cause-$cause"
        if (!AttributeSystem.fightGroupManager.containsKey(key)) return
        val data = FightData(attacker, defender) {
            it["origin"] = event.damage; it["event"] = event
            it["fightData"] = it
        }
        val result = FightAPI.runFight(key, data, damage = false)
        if (result > 0.0) {
            event.damage.amount = result.cfloat
            attacker?.intoFighting()
            defender.intoFighting()
        } else if (result < 0.0) {
            event.isCancelled = true
        }
    }
}