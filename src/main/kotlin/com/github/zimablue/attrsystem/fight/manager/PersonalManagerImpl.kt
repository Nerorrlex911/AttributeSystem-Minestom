package com.github.zimablue.attrsystem.fight.manager

import com.github.zimablue.attrsystem.fight.api.manager.PersonalManager
import com.github.zimablue.attrsystem.fight.internal.feature.personal.PersonalData
import com.github.zimablue.attrsystem.internal.feature.database.ASContainer
import com.github.zimablue.attrsystem.internal.manager.ASConfig
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import java.util.*

object PersonalManagerImpl : PersonalManager() {

    override val enable: Boolean
        get() = ASConfig.message.getBoolean("options.personal",true)

    @Awake(PluginLifeCycle.DISABLE)
    fun onDisable() {
        this.forEach {
            val player = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(it.key) ?: return
            pushData(player)
        }
    }

    override fun get(key: UUID): PersonalData {
        if (super.get(key) == null) {
            this[key] = PersonalData(key)
        }
        return if (!enable) {
            super.get(key)?.run {
                if (default) {
                    this
                } else {
                    default()
                    this
                }
            }!!
        } else super.get(key)!!
    }

    override fun pushData(player: Player) {
        if (enable)
            ASContainer[player.uuid, "personal-data"] = this[player.uuid].toString()
    }

    override fun pullData(player: Player): PersonalData? {
        return if (enable)
            PersonalData.fromStr(ASContainer[player.uuid, "personal-data"] ?: return null, player.uuid)
        else PersonalData(player.uuid)
    }

    override fun hasData(player: Player): Boolean {
        return ASContainer.contains(player.uuid, "personal-data")
    }


}
