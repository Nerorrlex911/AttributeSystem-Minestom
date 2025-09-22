package com.github.zimablue.attrsystem.api.manager

import com.github.zimablue.attrsystem.api.potion.PotionData
import com.github.zimablue.attrsystem.api.potion.PotionDataCompound
import com.github.zimablue.devoutserver.util.map.KeyMap
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.minestom.server.entity.LivingEntity
import java.util.UUID

abstract class PotionManager: KeyMap<UUID,PotionDataCompound>() {
    open fun addPotion(
        entity: LivingEntity,
        data: CompoundBinaryTag,
        source: String,
        duration: Long=-1,
        persistent: Boolean=false,
        removeOnDeath: Boolean=true,
    ) : Boolean {
        return addPotion(entity, com.github.zimablue.attrsystem.utils.toMap(data),source,duration,persistent,removeOnDeath)
    }
    /**
     * 给实体添加属性药水
     * 可以是字符串集合:
     * ```
     * {
     *   "type": "strings",
     *   "attributes": [
     *     "需要在地面上",
     *     "攻击力: 100 / 需要生命值属性: 10"
     *   ]
     * }
     * ```
     * 可以是NBT:
     * ```
     * {
     *   "type": "nbt",
     *   "attributes": {
     *     "ababa": {
     *       "PhysicalDamage": {
     *         "value": 100
     *       }
     *     }
     *   },
     *   "conditions": [
     *     {
     *       "conditions": [
     *         {
     *           "key": "ground",
     *           "status": true
     *         }
     *       ]
     *     },
     *     {
     *       "conditions": [
     *         {
     *           "key": "attribute",
     *           "name": "生命值",
     *           "value": 10
     *         }
     *       ]
     *     }
     *   ],
     *   "paths": [
     *     "ababa.PhysicalDamage.value"
     *   ]
     * }
     *
     * ```
     */
    abstract fun addPotion(
        entity: LivingEntity,
        data: Map<String,Any>,
        source: String,
        duration: Long=-1,
        persistent: Boolean=false,
        removeOnDeath: Boolean=true,
    ) : Boolean
    abstract fun addPotion(entity: LivingEntity, source: String, potionData: PotionData) : Boolean
    abstract fun removePotion(entity: LivingEntity, source: String)
}