package com.github.zimablue.attrsystem.internal.feature.compat.placeholder

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.AttributeSystem.attributeDataManager
import com.github.zimablue.attrsystem.AttributeSystem.attributeManager
import com.github.zimablue.attrsystem.AttributeSystem.compiledAttrDataManager
import com.github.zimablue.attrsystem.AttributeSystem.equipmentDataManager
import com.github.zimablue.attrsystem.api.attribute.Attribute
import com.github.zimablue.attrsystem.api.attribute.compound.AttributeDataCompound
import com.github.zimablue.attrsystem.api.compiled.sub.ComplexCompiledData
import com.github.zimablue.pouplaceholder.api.placeholder.PouPlaceHolder
import net.minestom.server.entity.LivingEntity

object AttributePlaceHolder : PouPlaceHolder("as", AttributeSystem.name,AttributeSystem.origin.authors.toString(),AttributeSystem.origin.version) {

    fun get(
        data: AttributeDataCompound,
        attribute: Attribute,
        params: List<String>,
    ): String {
        return when (params.size) {
            0 ->
                data.getAttrValue<Any>(attribute)?.toString()

            1 -> {
                data.getAttrValue<Any>(attribute, params[0])?.toString()
            }

            2 -> {
                data.getStatus(attribute)?.get(params[1])?.toString()
            }

            else ->
                "0.0"
        } ?: "0.0"
    }

    fun placeholder(params: String, entity: LivingEntity, attrData: AttributeDataCompound): String {
        val lower = params.lowercase().replace(":", "_")
        val uuid = entity.uuid
        val strings = if (lower.contains("_")) lower.split("_").toMutableList() else mutableListOf(lower)
        when (strings[0]) {
            "att" -> {
                val attribute = attributeManager[strings[1]]
                attribute?.also {
                    strings.removeAt(0)
                    strings.removeAt(0)
                    return get(attrData, attribute, strings)
                }
            }

            "equipment" -> {
                strings.removeAt(0)
                if (strings.size < 3) return "0.0"
                val source = strings[0]
                val slot = strings[1]
                val attKey = strings[2]
                strings.removeAt(0)
                strings.removeAt(0)
                strings.removeAt(0)
                val attribute = attributeManager[attKey] ?: return "0.0"
                val sourceKey = equipmentDataManager.getSource(source, slot)
                val compiledData = compiledAttrDataManager[uuid]?.get(sourceKey) ?: ComplexCompiledData()
                val itemAttrData = compiledData.eval(entity)
                return get(itemAttrData, attribute, strings)
            }
        }
        return "0.0"
    }

    override fun onPlaceHolderRequest(params: String, entity: LivingEntity, def: String): String {
        return placeholder(params, entity, attributeDataManager[entity.uuid] ?: return "NULL_DATA")
    }
}
