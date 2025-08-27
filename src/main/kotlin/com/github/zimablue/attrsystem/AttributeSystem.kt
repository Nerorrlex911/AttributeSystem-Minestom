package com.github.zimablue.attrsystem

import com.github.zimablue.attrsystem.api.AttributeSystemAPI
import com.github.zimablue.attrsystem.api.manager.*
import com.github.zimablue.attrsystem.internal.manager.*
import com.github.zimablue.devoutserver.plugin.Plugin

object AttributeSystem : Plugin() {

    val attributeManager : AttributeManager = AttributeManagerImpl
    val attributeDataManager : AttributeDataManager = AttributeDataManagerImpl
    val operationManager : OperationManager = OperationManagerImpl
    val readPatternManager: ReadPatternManager = ReadPatternManagerImpl
    val conditionManager : ConditionManager = ConditionManagerImpl
    val compileManager : CompileManager = CompileManagerImpl
    val compiledAttrDataManager : CompiledAttrDataManager = CompiledAttrDataManagerImpl
    val readManager : ReadManager = ReadManagerImpl
    val attributeSystemAPI : AttributeSystemAPI = AttributeSystemAPIImpl
    val equipmentDataManager: EquipmentDataManager = EquipmentDataManagerImpl

    override fun onEnable() {
        super.onEnable()
        logger.info("AttributeSystem enabled")

    }

}