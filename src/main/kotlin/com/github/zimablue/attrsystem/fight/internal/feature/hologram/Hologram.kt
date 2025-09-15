package com.github.zimablue.attrsystem.fight.internal.feature.hologram

import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import java.util.concurrent.ConcurrentHashMap

class Hologram(val instance: Instance, val pos: Pos, content: List<Component>) {

    val viewers = ConcurrentHashSet<Player>()
    constructor(instance: Instance, pos: Pos, content: List<Component>, vararg viewers: Player) : this(instance, pos, content) {
        this.viewers.addAll(viewers)
    }

    constructor(instance: Instance, pos: Pos, content: List<Component>, viewers: Set<Player>) : this(instance, pos, content) {
        this.viewers.addAll(viewers)
    }


    private val lines = ConcurrentHashMap<Int, HologramLine>()

    init {
        content.forEachIndexed { index, line ->
            lines[index] =
                HologramLine(instance,pos.add(0.0, (((content.size - 1) - index) * 0.3), 0.0), line, this.viewers)
        }
    }

    /**
     * Update line at index
     * if line not exist, create a new line
     */
    fun update(index: Int, text: Component) {
        if (!lines.containsKey(index)) {
            lines[index] =
                HologramLine(instance,pos.add(0.0, (((lines.size) - index) * 0.3), 0.0), text, this.viewers)
            return
        }
        lines[index]?.update(text)
    }

    /**
     * Update the whole hologram content
     * if new content has more lines, create new lines
     * if new content has fewer lines, remove extra lines
     */
    fun update(content: List<Component>) {
        // Update existing lines
        content.forEachIndexed { index, line ->
            if (lines.containsKey(index)) {
                lines[index]?.update(line)
            } else {
                lines[index] =
                    HologramLine(instance,pos.add(0.0, (((content.size - 1) - index) * 0.3), 0.0), line, this.viewers)
            }
        }
        // Remove extra lines
        val toRemove = lines.keys.filter { it >= content.size }
        toRemove.forEach {
            lines[it]?.remove()
            lines.remove(it)
        }
    }

    /**
     * Teleport the hologram to a new position in a possibly different instance
     */
    fun teleport(instance: Instance,pos: Pos) {
        // Same instance, just teleport
        if(instance==this.instance) {
            teleport(pos)
            return
        }
        lines.forEach { line ->
            line.value.setInstance(instance,pos.add(0.0, (((lines.size - 1) - line.key) * 0.3), 0.0))
        }
    }

    /**
     * Teleport the hologram to a new position in the same instance
     */
    fun teleport(pos: Pos) {
        lines.forEach { line ->
            line.value.teleport(pos.add(0.0, (((lines.size - 1) - line.key) * 0.3), 0.0))
        }
    }

    /**
     * remove the hologram, all lines will be removed
     */
    fun remove() {
        lines.forEach { it.value.remove() }
        lines.clear()
        viewers.clear()
    }

}