package com.github.zimablue.attrsystem.internal.feature.personal

import com.github.zimablue.attrsystem.AttributeSystem.attributeDataManager
import com.github.zimablue.attrsystem.api.attribute.compound.AttributeDataCompound
import com.github.zimablue.attrsystem.fight.manager.PersonalManagerImpl
import com.github.zimablue.attrsystem.internal.feature.database.ASContainer
import com.github.zimablue.devoutserver.util.map.component.Keyable
import com.google.gson.GsonBuilder
import net.minestom.server.entity.Player
import taboolib.common.util.unsafeLazy
import java.util.*

/**
 * @className InitialAttrData
 *
 * @author Glom
 * @date 2023/8/1 18:07 Copyright 2023 user. All rights reserved.
 */
class InitialAttrData(override val key: UUID, val compound: AttributeDataCompound = AttributeDataCompound()) :
    Keyable<UUID> {
    companion object {
        private val gson by unsafeLazy {
            GsonBuilder().create()
        }

        @JvmStatic
        fun deserialize(uuid: UUID, str: String): InitialAttrData? {

            return InitialAttrData(
                uuid,
                AttributeDataCompound.fromMap(gson.fromJson<Map<String, Any>>(str, Map::class.java) ?: return null)
            )
        }

        @JvmStatic
        fun fromPlayer(player: Player): InitialAttrData {
            return InitialAttrData(player.uuid, attributeDataManager[player.uuid] ?: AttributeDataCompound())
        }

        @JvmStatic
        internal fun pushAttrData(player: Player) {
            ASContainer[player.uuid,"initial-attr-data"] = fromPlayer(player).serialize()
        }

        @JvmStatic
        internal fun pullAttrData(uuid: UUID): InitialAttrData? {
            if(!PersonalManagerImpl.enable) return null
            val data = ASContainer[uuid, "initial-attr-data"] ?: return null
            if (data == "null") return null
            return deserialize(uuid, data)
        }
    }

    fun serialize(): String {
        return gson.toJson(compound.mapValues { it.value.serialize() })
    }
}