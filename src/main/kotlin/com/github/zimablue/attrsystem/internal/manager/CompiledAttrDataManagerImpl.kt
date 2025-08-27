package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem.readManager
import com.github.zimablue.attrsystem.api.compiled.CompiledAttrDataCompound
import com.github.zimablue.attrsystem.api.compiled.CompiledData
import com.github.zimablue.attrsystem.api.manager.CompiledAttrDataManager
import com.github.zimablue.attrsystem.utils.validEntity
import net.minestom.server.entity.LivingEntity
import java.util.*

object CompiledAttrDataManagerImpl : CompiledAttrDataManager() {
    override fun hasCompiledData(entity: LivingEntity, source: String): Boolean {
        return hasCompiledData(entity.uuid, source)
    }

    override fun hasCompiledData(uuid: UUID, source: String): Boolean {
        return get(uuid)?.containsKey(source) == true
    }

    override fun addCompiledData(
        entity: LivingEntity,
        source: String,
        attributes: Collection<String>,
        slot: String?,
    ): CompiledData? {
        return this.addCompiledData(entity.uuid, source, attributes, slot)
    }

    override fun addCompiledData(
        entity: LivingEntity,
        source: String,
        compiledData: CompiledData,
    ): CompiledData {
        val uuid = entity.uuid
        this.computeIfAbsent(uuid) { CompiledAttrDataCompound(entity) }.register(source, compiledData)
        return compiledData
    }

    override fun addCompiledData(
        uuid: UUID,
        source: String,
        attributes: Collection<String>,
        slot: String?,
    ): CompiledData? {
        return readManager.read(attributes, uuid.validEntity(), slot)?.let {
            this.addCompiledData(
                uuid,
                source,
                it
            )
        }
    }

    override fun addCompiledData(uuid: UUID, source: String, compiledData: CompiledData): CompiledData? {
        return uuid.validEntity()?.let { addCompiledData(it, source, compiledData) }
    }

    override fun removeCompiledData(entity: LivingEntity, source: String): CompiledData? {
        return removeCompiledData(entity.uuid, source)
    }

    override fun removeCompiledData(uuid: UUID, source: String): CompiledData? {
        return this[uuid]?.run {
            remove(source)
        }
    }

    override fun removeIfStartWith(entity: LivingEntity, prefix: String) {
        return removeIfStartWith(entity.uuid, prefix)
    }

    override fun get(key: UUID): CompiledAttrDataCompound? {
        val entity = key.validEntity() ?: return null
        return computeIfAbsent(key) { CompiledAttrDataCompound(entity) }
    }

    override fun removeIfStartWith(uuid: UUID, prefix: String) {
        val lower = prefix.lowercase()
        this[uuid]?.run {
            filterKeys { it.startsWith(lower) }.map { it.key }.forEach(this::remove)
        }
    }


}
