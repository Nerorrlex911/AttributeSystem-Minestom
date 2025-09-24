package com.github.zimablue.attrsystem.api.manager

import net.minestom.server.entity.Player

abstract class HealthManager {
    abstract fun getMaxHealth(player: Player) : Double
    abstract fun getHealth(player: Player) : Double
    abstract fun setHealth(player: Player, health: Double)
}