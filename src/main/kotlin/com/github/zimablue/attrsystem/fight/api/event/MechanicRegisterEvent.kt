package com.github.zimablue.attrsystem.fight.api.event

import com.github.zimablue.attrsystem.fight.api.fight.mechanic.Mechanic
import net.minestom.server.event.trait.CancellableEvent

/**
 * Mechanic register event
 *
 * @constructor Create empty Mechanic register event
 * @property mechanic 机制
 */
class MechanicRegisterEvent(val mechanic: Mechanic) : CancellableEvent {
    private var isCancelled = false
    override fun isCancelled(): Boolean = isCancelled
    override fun setCancelled(cancel: Boolean) {
        isCancelled = cancel
    }
}
