package com.github.zimablue.attrsystem

import com.github.zimablue.attrsystem.api.AttributeSystemAPI
import com.github.zimablue.attrsystem.api.manager.*
import com.github.zimablue.attrsystem.fight.api.manager.*
import com.github.zimablue.attrsystem.fight.manager.*
import com.github.zimablue.attrsystem.internal.core.command.ASCommand
import com.github.zimablue.attrsystem.internal.feature.compat.placeholder.AttributePlaceHolder
import com.github.zimablue.attrsystem.internal.manager.*
import com.github.zimablue.attrsystem.internal.manager.ASConfig.debug
import com.github.zimablue.attrsystem.utils.console
import com.github.zimablue.devoutserver.lang.LangManager
import com.github.zimablue.devoutserver.lang.LangManagerImpl
import com.github.zimablue.devoutserver.plugin.Plugin
import com.github.zimablue.devoutserver.plugin.lang.PluginLangManager
import com.github.zimablue.pouplaceholder.PouPlaceholder
import net.minestom.server.MinecraftServer
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import kotlin.math.log

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

    val potionManager: PotionManager = PotionManagerImpl
    val healthManager: HealthManager = HealthManagerImpl

    val langManager = PluginLangManager(this)

    //fight system managers
    val mechanicManager: MechanicManager = MechanicManagerImpl
    val messageBuilderManager: MessageBuilderManager = MessageBuilderManagerImpl

    val damageTypeManager: DamageTypeManager = DamageTypeManagerImpl

    val fightGroupManager: FightGroupManager = FightGroupManagerImpl
    
    val personalManager: PersonalManager = PersonalManagerImpl
    
    val fightStatusManager: FightStatusManager = FightStatusManagerImpl

    override fun onLoad() {
        super.onLoad()
        // 使懒加载的langManager初始化
        langManager.init()
    }

    override fun onEnable() {
        super.onEnable()
        logger.info("AttributeSystem enabled")
        MinecraftServer.getCommandManager().register(ASCommand)
        PouPlaceholder.placeholderManager.register(AttributePlaceHolder)

    }

    override fun onActive() {
        super.onActive()
        //todo some assertions for testing
        debug {
            logger.info("AttributeSystem active")
        }
        debug{
            logger.info("readMap1: ")
            logger.info(readManager.readMap(mapOf(
                "type" to "strings",
                "attributes" to listOf("物理伤害: 100","生命值: 10")
            ))?.serialize().toString())
            logger.info("readMap2: ")
            val configMap: Map<String, Any> = mapOf(
                "type" to "nbt",
                "attributes" to mapOf(
                    "ababa" to mapOf(
                        "PhysicalDamage" to mapOf(
                            "value" to 100
                        )
                    )
                ),
                "conditions" to emptyList<Any>()
//                "conditions" to listOf(
//                    mapOf(
//                        "conditions" to listOf(
//                            mapOf(
//                                "key" to "ground",
//                                "status" to true
//                            )
//                        )
//                    ),
//                    mapOf(
//                        "conditions" to listOf(
//                            mapOf(
//                                "key" to "attribute",
//                                "name" to "生命值",
//                                "value" to 10
//                            )
//                        ),
//                        "paths" to listOf("ababa.PhysicalDamage.value")
//                    )
//                )
            )
            logger.info(readManager.readMap(configMap)?.serialize().toString())
        }
    }

}