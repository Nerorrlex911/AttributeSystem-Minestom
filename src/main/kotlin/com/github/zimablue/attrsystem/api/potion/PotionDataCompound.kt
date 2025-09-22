package com.github.zimablue.attrsystem.api.potion

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.devoutserver.util.map.BaseMap
import com.github.zimablue.devoutserver.util.map.component.Registrable
import com.google.gson.GsonBuilder
import taboolib.common.util.unsafeLazy
import java.util.*

class PotionDataCompound(override val key: UUID) : Registrable<UUID>, BaseMap<String, PotionData>() {
    override fun register() {
        AttributeSystem.potionManager.register(this)
    }
    fun release() {
        forEach{
            it.value.release()
        }
    }
    fun serialize() : String {
        val map = mutableMapOf<String, PotionData>()
        // 只有persistent=true的药水效果需要保存
        forEach{
            if(it.value.persistent) map[it.key] = it.value.resetDuration()
        }
        return gson.toJson(map)
    }
    companion object {
        private val gson by unsafeLazy {
            GsonBuilder().create()
        }
        fun deserialize(key: UUID,data: String): PotionDataCompound {
            val compound = PotionDataCompound(key)
            compound.putAll(gson.fromJson<Map<String,PotionData>>(data,Map::class.java))
            return compound
        }
    }
}