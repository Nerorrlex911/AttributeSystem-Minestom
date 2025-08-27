package com.github.zimablue.attrsystem.internal.feature.database

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.internal.manager.ASConfig
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import com.github.zimablue.playerdatabase.core.DatabaseHandler
import java.io.File
import java.util.*

object ASContainer {

    private val databaseHandler by lazy { DatabaseHandler(AttributeSystem) }

    private val databaseType: String
        get() = ASConfig.config.getString("database.type")?:"sqlite"


    operator fun get(user: UUID, key: String): String? {
        return databaseHandler.getPlayerDataContainer(user)[key]
    }

    fun delete(user: UUID, key: String) {
        return databaseHandler.getPlayerDataContainer(user).delete(key)
    }

    operator fun set(user: UUID, key: String, value: String?) {
        databaseHandler.getPlayerDataContainer(user)[key] = value?:""
    }

    fun contains(user: UUID, key: String): Boolean {
        return databaseHandler.getPlayerDataContainer(user).keys().contains(key)
    }

    @Awake(PluginLifeCycle.ENABLE)
    fun onEnable() {
        if (databaseType.equals("sqlite", true)) {
            databaseHandler.setupPlayerDatabase(
                File(ASConfig.config.getString("database.path")?:"plugins/AttributeSystem/database.db")
            )
        } else {
            databaseHandler.setupPlayerDatabase(ASConfig.config.getConfigurationSection("database")!!)
        }

    }

}