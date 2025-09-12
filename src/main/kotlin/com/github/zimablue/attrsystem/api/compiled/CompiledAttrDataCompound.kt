package com.github.zimablue.attrsystem.api.compiled

import com.github.zimablue.attrsystem.api.attribute.compound.AttributeDataCompound
import com.github.zimablue.attrsystem.api.compiled.sub.ComplexCompiledData
import com.github.zimablue.attrsystem.internal.feature.attribute.BaseAttributeEntity.baseEntity
import com.github.zimablue.attrsystem.internal.feature.attribute.BaseAttributePlayer.basePlayer
import com.github.zimablue.devoutserver.util.map.LowerMap
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player

class CompiledAttrDataCompound(entity: LivingEntity) : LowerMap<CompiledData>(), Evalable {

    init {
        if (entity is Player)
            basePlayer()
        else
            baseEntity()
    }

    override fun eval(entity: LivingEntity?): AttributeDataCompound {
        val result = AttributeDataCompound(entity)
        val total = ComplexCompiledData()
        forEach { (_, compiledData) ->
            total.add(compiledData)
        }
        val maxLayers = total.layers(1)
        result.combine(total.eval(entity))
        repeat(maxLayers) {
            result.putAll(total.eval(entity))
        }
        return result.allToRelease()

    }

    fun serialize(): MutableMap<String, Any> {
        val children = LinkedHashMap<String, Any>()
        this.entries.forEach { (key, data) ->
            children[key] = data.serialize()
        }
        return linkedMapOf(
            "CompiledAttrDataCompound" to children
        )
    }

}