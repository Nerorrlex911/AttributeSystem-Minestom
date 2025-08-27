package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem.conditionManager
import com.github.zimablue.attrsystem.api.attribute.compound.AttributeData
import com.github.zimablue.attrsystem.api.compiled.sub.ComplexCompiledData
import com.github.zimablue.attrsystem.api.compiled.sub.NBTCompiledData
import com.github.zimablue.attrsystem.api.compiled.sub.StringsCompiledData
import com.github.zimablue.attrsystem.api.manager.CompileManager
import net.minestom.server.entity.LivingEntity

object CompileManagerImpl : CompileManager() {
    override fun compile(
        entity: LivingEntity?,
        nbt: Collection<Any>,
        slot: String?,
    ): (MutableMap<String, Any>) -> NBTCompiledData {
        return { attrDataMap ->
            val total = NBTCompiledData(attrDataMap)
            for (condCompound in nbt) {
                condCompound as? Map<String, Any> ?: continue
                val paths = condCompound["paths"] as? List<String> ?: continue
                val entry = NBTCompiledData.Entry(paths)
                val conditions = condCompound["conditions"] as? List<Map<String, Any>> ?: continue
                conditionManager.matchConditions(conditions, slot).forEach(entry::register)
                total.add(entry)
            }
            total
        }

    }

    override fun compile(
        entity: LivingEntity?,
        string: String,
        slot: String?,
    ): ((AttributeData) -> StringsCompiledData)? {
        val matches = conditionManager.matchConditions(string, slot)
        return if (matches.isNotEmpty())
            { data ->
                StringsCompiledData(data).apply {
                    matches.forEach(this::register)
                }
            }
        else null
    }

    override fun mapping(entity: LivingEntity?): (AttributeData) -> ComplexCompiledData = { data ->
        ComplexCompiledData().apply {
            data.forEach { (attribute, status) ->
                attribute.mapping?.mapping(status, entity)?.also(this::add)
            }
        }
    }


}
