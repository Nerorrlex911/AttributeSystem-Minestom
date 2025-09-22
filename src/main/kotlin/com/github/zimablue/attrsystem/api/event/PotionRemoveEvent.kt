package com.github.zimablue.attrsystem.api.event

import com.github.zimablue.attrsystem.api.potion.PotionData
import net.minestom.server.entity.LivingEntity
import net.minestom.server.event.trait.CancellableEvent

class PotionRemoveEvent(
    val entity: LivingEntity,
    val source: String,
    val potionData: PotionData?
) : CancellableEvent {
    private var isCancelled = false
    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(p0: Boolean) {
        isCancelled = p0
    }
}