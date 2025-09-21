package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.api.manager.OperationManager
import com.github.zimablue.attrsystem.internal.feature.operation.num.*
import com.github.zimablue.attrsystem.internal.feature.operation.str.OperationAppend
import com.github.zimablue.attrsystem.internal.feature.operation.str.OperationSkip
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.AwakePriority
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle

object OperationManagerImpl: OperationManager() {

    private fun registerDefault() {
        register(OperationMax)
        register(OperationMin)
        register(OperationPlus)
        register(OperationReduce)
        register(OperationScalar)
        register(OperationAppend)
        register(OperationSkip)
    }

    @Awake(PluginLifeCycle.ENABLE,AwakePriority.LOWEST)
    fun onEnable() {
        registerDefault()
        onReload()
    }

    @Awake(PluginLifeCycle.RELOAD,AwakePriority.LOWEST)
    fun onReload() {
        this.entries.filter { it.value.release }.forEach { this.remove(it.key) }
    }
}