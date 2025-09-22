package com.github.zimablue.attrsystem.internal.core.command

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.api.attribute.compound.AttributeDataCompound
import com.github.zimablue.attrsystem.internal.core.schedule.TaskScheduler
import com.github.zimablue.attrsystem.internal.manager.ASConfig
import com.github.zimablue.attrsystem.utils.getName
import com.github.zimablue.attrsystem.utils.sendLang
import com.github.zimablue.attrsystem.utils.toMiniMessage
import com.github.zimablue.attrsystem.utils.toPlain
import com.github.zimablue.devoutserver.util.colored
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.suggestion.SuggestionEntry
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.utils.entity.EntityFinder

object StatsCommand {
    
    fun EntityFinder.findFirstLiving(sender: CommandSender): LivingEntity? {
        return this.find(sender).firstOrNull { it is LivingEntity } as? LivingEntity
    }

    val stats = object : Command("stats") {
        val argEntity = ArgumentType.Entity("target").singleEntity(true)
        init {
            setDefaultExecutor { sender, context ->
                if (sender !is Player) {
                    sender.sendLang("command-info")
                    return@setDefaultExecutor
                }
                sendStatText(sender,sender)
            }
            addSyntax({ sender, context ->
                val entity = context.get(argEntity).findFirstLiving(sender)?:return@addSyntax
                sendStatText(sender,entity as? LivingEntity?:return@addSyntax)
            },argEntity)
        }
    }

    val itemStats = object : Command("itemstats") {
        val argEntity = ArgumentType.Entity("target").singleEntity(true)
        val key = ArgumentType.String("key").setSuggestionCallback { sender, context, suggestion ->
            val entity = context.get(argEntity).findFirstLiving(sender)?: run {
                sender.sendLang("command-valid-entity")
                return@setSuggestionCallback
            }
            AttributeSystem.equipmentDataManager[entity.uuid]?.map { suggestion.addEntry(SuggestionEntry(it.key)) }
        }
        val slot = ArgumentType.String("slot").setSuggestionCallback { sender, context, suggestion ->
            val entity = context.get(argEntity).findFirstLiving(sender)?: run {
                sender.sendLang("command-valid-entity")
                return@setSuggestionCallback
            }
            val key = context.get(key)
            AttributeSystem.equipmentDataManager[entity.uuid]?.get(key)?.map { suggestion.addEntry(SuggestionEntry(it.key)) }
        }
        init {
            addSyntax({ sender, context ->
                val entity = context.get(argEntity).findFirstLiving(sender)?: run {
                    sender.sendLang("command-valid-entity")
                    return@addSyntax
                }
                entity as LivingEntity
                val source = context.get(key)
                val slot = context.get(slot)
                val itemStack = AttributeSystem.equipmentDataManager[entity.uuid]?.get(source,slot)
                if(itemStack == null||itemStack.isAir) {
                    sender.sendLang("command-valid-item")
                    return@addSyntax
                }
                TaskScheduler.scheduleOnce("itemstats") {
                    val data =
                        AttributeSystem.compiledAttrDataManager[entity.uuid]?.get(
                            AttributeSystem.equipmentDataManager.getSource(
                                source,
                                slot
                            )
                        )?.eval(entity) ?: AttributeDataCompound()
                    sendStatText(sender, entity, itemStack.getName(), data, true)
                }
            },argEntity,key,slot)
        }
    }

    val entityStats = object : Command("entitystats") {
        init {
            setDefaultExecutor { player, context ->
                if (player !is Player) {
                    return@setDefaultExecutor
                }
                val entity = player.getLineOfSightEntity(10.0){it is LivingEntity} as? LivingEntity
                if(entity == null) {
                    player.sendLang("command-valid-entity")
                    return@setDefaultExecutor
                }
                TaskScheduler.scheduleOnce("entitystats") {
                    sendStatText(player, entity)
                }
            }
        }
    }

    private fun attributeStatusToJson(
        data: AttributeDataCompound,
        entity: LivingEntity,
        item: Boolean = false,
    ): ArrayList<Component> {
        val attributes = AttributeSystem.attributeManager.attributes
        val list = ArrayList<Component>()
        for (index in attributes.indices) {
            val attribute = attributes[index]
            if (!attribute.entity && !item) continue
            val status = data.getStatus(attribute) ?: continue
            val json = attribute.readPattern.stat(
                attribute,
                status,
                entity
            )
            list.add(json)
        }
        return list
    }

    private fun sendStatText(
        sender: CommandSender,
        entity: LivingEntity,
        name: String = entity.getDisplayName(),
        data: AttributeDataCompound = AttributeSystem.attributeDataManager[entity.uuid]
            ?: AttributeDataCompound(),
        item: Boolean = false,
    ) {
        val legacy = LegacyComponentSerializer.legacySection()
        val title = ASConfig.statsTitle.replace("{name}", name).replace("{player}", name).colored()
        sender.sendMessage(" ")
        sender.sendMessage(legacy.deserialize(title))
        sender.sendMessage(" ")
        attributeStatusToJson(data, entity, item).forEach {
            sender.sendMessage(it)
        }
        sender.sendMessage(" ")
        sender.sendMessage(legacy.deserialize(ASConfig.statsEnd))
    }

    private fun LivingEntity.getDisplayName() : String {
        return (this as? Player)?.displayName?.toPlain()
            ?: ((if (this.customName == null) this.entityType.name() else this.customName?.toPlain()) ?: "null")
    }
}