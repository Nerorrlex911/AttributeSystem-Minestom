package com.github.zimablue.attrsystem.fight.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.fight.api.fight.DataCache
import com.github.zimablue.attrsystem.internal.manager.ASConfig
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import net.minestom.server.entity.Entity
import net.minestom.server.entity.LivingEntity
import net.minestom.server.event.entity.EntityShootEvent
import net.minestom.server.event.entity.projectile.ProjectileCollideWithEntityEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object ProjectileRealizer {

    val caches = ConcurrentHashMap<UUID, DataCache>()

    val defaultEnable: Boolean
        get() = ASConfig.config.getBoolean("options.fight.projectile-cache-data",true)

    const val CACHE_KEY = "ATTRIBUTE_SYSTEM_DATA"

    @Awake(PluginLifeCycle.ENABLE)
    fun onEnable() {
        AttributeSystem.asEventNode.addListener(EntityShootEvent::class.java) { event ->
            projectileLaunch(event)
        }
    }

    fun projectileLaunch(event: EntityShootEvent) {
        val projectile = event.projectile
        val shooter = (event.entity as? LivingEntity?) ?: return
        val cacheData = DataCache().attacker(shooter)
        projectile.cache(cacheData)
    }


    fun projectileHit(event: ProjectileCollideWithEntityEvent) {
        val projectile = event.entity
        val hitEntity = (event.target as? LivingEntity?) ?: return
        val velocity = projectile.velocity.sub(hitEntity.velocity).length() / 2.0
        //todo charge
        //projectile.setTag(CHARGE_KEY, velocity)
    }

    fun Entity.cache(data: DataCache) {
        caches[this.uuid] = data
    }

    fun Entity.cache(): DataCache? =
        caches[this.uuid]

    fun Entity.charged(): Double? = 0.0
//        if (hasMeta(CHARGE_KEY)) getMeta(CHARGE_KEY)[0].asDouble() else null
}