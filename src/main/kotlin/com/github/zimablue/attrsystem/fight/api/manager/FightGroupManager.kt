package com.github.zimablue.attrsystem.fight.api.manager

import com.github.zimablue.attrsystem.fight.api.fight.FightData
import com.github.zimablue.attrsystem.fight.internal.core.fight.FightGroup
import com.github.zimablue.devoutserver.util.map.KeyMap

/**
 * Fight group manager
 *
 * @constructor Create empty Fight group manager
 */
abstract class FightGroupManager : KeyMap<String, FightGroup>() {
    abstract fun runFight(key: String, data: FightData, message: Boolean = true, damage: Boolean = true): Double
}
