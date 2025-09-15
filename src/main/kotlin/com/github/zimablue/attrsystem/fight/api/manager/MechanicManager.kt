package com.github.zimablue.attrsystem.fight.api.manager

import com.github.zimablue.attrsystem.fight.api.fight.mechanic.Mechanic
import com.github.zimablue.devoutserver.util.map.KeyMap

/**
 * Mechanic manager
 *
 * @constructor Create empty Mechanic manager
 */
abstract class MechanicManager : KeyMap<String, Mechanic>()
