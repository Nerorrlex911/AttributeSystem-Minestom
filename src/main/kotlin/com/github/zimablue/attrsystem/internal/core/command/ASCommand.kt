package com.github.zimablue.attrsystem.internal.core.command

import com.github.zimablue.attrsystem.internal.manager.ASConfig
import net.minestom.server.command.builder.Command


object ASCommand : Command("as","attrsystem","attsystem") {

    init {
        setDefaultExecutor { sender, context ->
            sender.sendMessage(ASConfig.lang.getStringList("command-info").toTypedArray())
        }
        //addSubcommand(StatsCommand)
        addSubcommand(StatsCommand.stats)
        addSubcommand(StatsCommand.itemStats)
        addSubcommand(StatsCommand.entityStats)
        addSubcommand(PotionCommand.potion)
    }

}