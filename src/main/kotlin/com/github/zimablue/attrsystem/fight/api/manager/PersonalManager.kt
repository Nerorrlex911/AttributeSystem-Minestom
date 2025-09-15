package com.github.zimablue.attrsystem.fight.api.manager


import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.fight.internal.feature.personal.PersonalData
import com.github.zimablue.devoutserver.util.map.KeyMap
import net.minestom.server.entity.Player
import java.util.*

/**
 * Personal manager
 *
 * @constructor Create empty Personal manager
 */
abstract class PersonalManager : KeyMap<UUID, PersonalData>() {

    /** Enable */
    abstract val enable: Boolean

    /**
     * Push data
     *
     * @param player
     */
    abstract fun pushData(player: Player)

    /**
     * Pull data
     *
     * @param player
     * @return
     */
    abstract fun pullData(player: Player): PersonalData?

    /**
     * Has data
     *
     * @param player
     * @return
     */
    abstract fun hasData(player: Player): Boolean

    companion object {
        internal fun Player.pushData() {
            AttributeSystem.personalManager.pushData(this)
        }

        internal fun Player.pullData(): PersonalData? =
            AttributeSystem.personalManager.pullData(this)

        internal fun Player.hasData(): Boolean = AttributeSystem.personalManager.hasData(this)
    }
}
