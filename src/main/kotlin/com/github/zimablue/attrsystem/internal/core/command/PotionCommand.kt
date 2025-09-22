package com.github.zimablue.attrsystem.internal.core.command

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.internal.core.command.StatsCommand.findFirstLiving
import com.github.zimablue.attrsystem.utils.sendLang
import com.github.zimablue.attrsystem.utils.toMap
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType

object PotionCommand {
    val potion = object : Command("potion") {
        val argEntity = ArgumentType.Entity("target").singleEntity(true)
        val source = ArgumentType.String("source")
        val duration = ArgumentType.Long("duration")
        val nbt = ArgumentType.NbtCompound("nbt")
        val persistent = ArgumentType.Boolean("persistent")
        val removeOnDeath = ArgumentType.Boolean("removeOnDeath")
        val add = ArgumentType.Literal("add")
        val remove = ArgumentType.Literal("remove")
        init {
            setDefaultExecutor { sender, context ->
                sender.sendLang("command-info")
            }
            addSyntax({ sender, context ->
                val entity = context.get(argEntity).findFirstLiving(sender)?:return@addSyntax
                val source = context.get(source)
                val nbt = toMap(context.get(nbt))
                val duration = context.get(duration)
                val persistent = context.get(persistent)
                val removeOnDeath = context.get(removeOnDeath)
                AttributeSystem.potionManager.addPotion(
                    entity,
                    nbt,
                    source,
                    duration,
                    persistent,
                    removeOnDeath,
                )
            },add,argEntity,nbt,source,duration,persistent,removeOnDeath)
            addSyntax({ sender, context ->
                val entity = context.get(argEntity).findFirstLiving(sender)?:return@addSyntax
                val source = context.get(source)
                AttributeSystem.potionManager.removePotion(entity,source)
            },remove,argEntity,source)
        }
    }
}