package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.api.manager.OperationManager
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.AwakePriority
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle

object OperationManagerImpl: OperationManager() {

    @Awake(PluginLifeCycle.ENABLE,AwakePriority.NORMAL)
    fun onEnable() {
        onReload()
    }

    @Awake(PluginLifeCycle.RELOAD,AwakePriority.NORMAL)
    fun onReload() {
        this.entries.filter { it.value.release }.forEach { this.remove(it.key) }
    }
}