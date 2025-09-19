package com.github.zimablue.attrsystem.fight.internal.feature.personal

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.fight.api.manager.PersonalManager.Companion.pullData
import com.github.zimablue.attrsystem.fight.api.manager.PersonalManager.Companion.pushData
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerDisconnectEvent

object Personal {

    @Awake(PluginLifeCycle.ENABLE)
    fun onEnable() {
        AttributeSystem.asEventNode
            .addListener(AsyncPlayerConfigurationEvent::class.java) {event ->
                (event.player.pullData() ?: PersonalData(event.player.uuid)).register()
            }
            .addListener(PlayerDisconnectEvent::class.java) {event ->
                event.player.pushData()
            }
    }

}