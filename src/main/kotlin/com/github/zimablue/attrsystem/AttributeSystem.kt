package com.github.zimablue.attrsystem

import com.github.zimablue.attrsystem.api.AttributeSystemAPI
import com.github.zimablue.attrsystem.api.manager.*
import com.github.zimablue.attrsystem.fight.api.manager.*
import com.github.zimablue.attrsystem.fight.manager.MechanicManagerImpl
import com.github.zimablue.attrsystem.fight.manager.MessageBuilderManagerImpl
import com.github.zimablue.attrsystem.internal.core.command.ASCommand
import com.github.zimablue.attrsystem.internal.manager.*
import com.github.zimablue.devoutserver.plugin.Plugin
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.EntityType
import net.minestom.server.event.EventNode

object AttributeSystem : Plugin() {

    val asEventNode = EventNode.all("AttributeSystem-Node")

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

    //fight system managers
    val mechanicManager: MechanicManager = MechanicManagerImpl
    val messageBuilderManager: MessageBuilderManager = MessageBuilderManagerImpl

    lateinit var damageTypeManager: DamageTypeManager

    lateinit var fightGroupManager: FightGroupManager
    
    lateinit var personalManager: PersonalManager
    
    lateinit var fightStatusManager: FightStatusManager

    override fun onEnable() {
        super.onEnable()
        logger.info("AttributeSystem enabled")
        MinecraftServer.getCommandManager().register(ASCommand)

    }

    override fun onActive() {
        super.onActive()
        //todo some assertions for testing
    }

}