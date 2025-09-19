package com.github.zimablue.attrsystem

import com.github.zimablue.attrsystem.api.AttributeSystemAPI
import com.github.zimablue.attrsystem.api.manager.*
import com.github.zimablue.attrsystem.fight.api.manager.*
import com.github.zimablue.attrsystem.fight.manager.*
import com.github.zimablue.attrsystem.internal.core.command.ASCommand
import com.github.zimablue.attrsystem.internal.manager.*
import com.github.zimablue.attrsystem.internal.manager.ASConfig.debug
import com.github.zimablue.devoutserver.plugin.Plugin
import net.minestom.server.MinecraftServer
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode

object AttributeSystem : Plugin() {

    val asEventNode: EventNode<Event> = EventNode.all("AttributeSystem-Node")

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

    val damageTypeManager: DamageTypeManager = DamageTypeManagerImpl

    val fightGroupManager: FightGroupManager = FightGroupManagerImpl
    
    val personalManager: PersonalManager = PersonalManagerImpl
    
    val fightStatusManager: FightStatusManager = FightStatusManagerImpl

    override fun onEnable() {
        super.onEnable()
        logger.info("AttributeSystem enabled")
        MinecraftServer.getCommandManager().register(ASCommand)

    }

    override fun onActive() {
        super.onActive()
        //todo some assertions for testing
        debug {
            logger.info("AttributeSystem active")
            logger.info("loaded scripts: ${ScriptManager.pluginScriptManager.compiledScripts.keys}")
        }
    }

}