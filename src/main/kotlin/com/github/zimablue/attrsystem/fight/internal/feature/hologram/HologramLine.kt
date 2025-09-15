package com.github.zimablue.attrsystem.fight.internal.feature.hologram

import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.instance.Instance

class HologramLine(instance: Instance, pos: Pos, text: Component): Entity(EntityType.TEXT_DISPLAY) {
    init {
        this.setInstance(instance, pos)
        (this.entityMeta as TextDisplayMeta).text = text
        this.isAutoViewable = false
    }
    constructor(instance: Instance, pos: Pos, text: Component, viewers: Set<Player>) : this(instance, Pos(pos), text) {
        viewers.forEach { addViewer(it) }
    }
    fun update(text: Component) {
        (this.entityMeta as TextDisplayMeta).text = text
    }
}