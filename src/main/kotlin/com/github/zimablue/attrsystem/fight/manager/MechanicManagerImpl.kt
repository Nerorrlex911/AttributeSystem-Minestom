package com.github.zimablue.attrsystem.fight.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.fight.api.event.MechanicRegisterEvent
import com.github.zimablue.attrsystem.fight.api.fight.mechanic.Mechanic
import com.github.zimablue.attrsystem.fight.api.manager.MechanicManager
import com.github.zimablue.attrsystem.internal.manager.ASConfig.debug
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import net.minestom.server.event.EventDispatcher

object MechanicManagerImpl: MechanicManager() {
    @Awake(PluginLifeCycle.ENABLE)
    fun onEnable() {
        onReload()
    }
    @Awake(PluginLifeCycle.RELOAD)
    fun onReload() {
        this.entries.filter { it.value.release }.forEach { this.remove(it.key) }
    }

    override fun register(key: String, value: Mechanic): Mechanic? {
        val event = MechanicRegisterEvent(value)
        EventDispatcher.call(event)
        debug {
            AttributeSystem.logger.info("机制注册 key: $key, value: $value")
        }
        if (event.isCancelled) return null
        return put(key, value)
    }
}