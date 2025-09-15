package com.github.zimablue.attrsystem.fight.api.event

import com.github.zimablue.attrsystem.fight.api.fight.mechanic.Mechanic
import net.minestom.server.event.Event
import java.util.*

/**
 * Mechanic register event
 *
 * @constructor Create empty Mechanic register event
 * @property mechanic 机制
 */
class MechanicLoadEvent(val key:String,var mechanic: Optional<Mechanic> = Optional.empty()) : Event
