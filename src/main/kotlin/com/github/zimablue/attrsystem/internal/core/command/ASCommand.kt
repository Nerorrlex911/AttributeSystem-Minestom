package com.github.zimablue.attrsystem.internal.core.command

import com.github.zimablue.attrsystem.AttributeSystem.langManager
import com.github.zimablue.attrsystem.internal.manager.ASConfig
import net.minestom.server.command.builder.Command


object ASCommand : Command("as","attrsystem","attsystem") {

    init {
        setDefaultExecutor { sender, context ->
            langManager.sendLang(sender, "command-info")
        }
        //addSubcommand(StatsCommand)
        addSubcommand(StatsCommand.stats)
        addSubcommand(StatsCommand.itemStats)
        addSubcommand(StatsCommand.entityStats)
        addSubcommand(PotionCommand.potion)
    }

}