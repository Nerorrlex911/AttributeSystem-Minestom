package com.github.zimablue.attrsystem.fight.api.manager

import com.github.zimablue.attrsystem.fight.api.fight.DamageType
import com.github.zimablue.devoutserver.util.map.KeyMap

/**
 * Damage type manager
 *
 * @constructor Create empty Damage type manager
 */
abstract class DamageTypeManager : KeyMap<String, DamageType>()
