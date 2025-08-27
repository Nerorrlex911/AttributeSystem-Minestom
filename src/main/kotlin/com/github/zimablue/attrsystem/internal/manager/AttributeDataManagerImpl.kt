package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem.attributeDataManager
import com.github.zimablue.attrsystem.AttributeSystem.compileManager
import com.github.zimablue.attrsystem.AttributeSystem.compiledAttrDataManager
import com.github.zimablue.attrsystem.api.attribute.compound.AttributeData
import com.github.zimablue.attrsystem.api.attribute.compound.AttributeDataCompound
import com.github.zimablue.attrsystem.api.event.AttributeUpdateEvent
import com.github.zimablue.attrsystem.api.manager.AttributeDataManager
import com.github.zimablue.attrsystem.internal.feature.personal.InitialAttrData.Companion.pullAttrData
import com.github.zimablue.attrsystem.utils.validEntity
import com.github.zimablue.attrsystem.utils.isAlive
import net.minestom.server.entity.LivingEntity
import net.minestom.server.event.EventDispatcher
import java.util.*

object AttributeDataManagerImpl: AttributeDataManager() {
    override fun get(key: UUID): AttributeDataCompound? {
        return uncheckedGet(key) ?: pullAttrData(key)?.compound
    }

    private fun uncheckedGet(key: UUID): AttributeDataCompound? {
        return super.get(key)
    }

    override fun update(entity: LivingEntity): AttributeDataCompound? {
        if (!entity.isAlive()) return null
        val uuid = entity.uuid
        var attrData =
            uncheckedGet(uuid)?.clone() ?: AttributeDataCompound(entity).also { this[uuid] = it }
        //PRE
        val preEvent =
            AttributeUpdateEvent.Pre(entity, attrData)
        EventDispatcher.call(preEvent)
        attrData = preEvent.data
        attrData.release()
        //PROCESS

        compiledAttrDataManager[uuid]?.apply {
            attrData.combine(eval(entity))
        }

        val process =
            AttributeUpdateEvent.Process(entity, attrData)
        EventDispatcher.call(process)
        attrData = process.data
        this[uuid] = attrData
        attrData.init()

        attrData.combine(compileManager.mapping(entity)(attrData.toAttributeData()).eval(entity).allToRelease())

        //AFTER
        val postEvent =
            AttributeUpdateEvent.Post(entity, attrData)
        EventDispatcher.call(postEvent)
        attrData = postEvent.data
        this[uuid] = attrData
        return attrData
    }

    override fun addAttrData(
        entity: LivingEntity,
        source: String,
        attributeData: AttributeData,

        ): AttributeData {
        if (!entity.isAlive()) {
            return attributeData
        }
        val uuid = entity.uuid
        if (attributeDataManager.containsKey(uuid)) {
            attributeDataManager[uuid]!!.register(source, attributeData)
        } else {
            val compound = AttributeDataCompound()
            compound.register(source, attributeData)
            attributeDataManager.register(uuid, compound)
        }
        return attributeData
    }

    override fun addAttrData(uuid: UUID, source: String, attributeData: AttributeData): AttributeData {
        return uuid.validEntity()?.let { addAttrData(it, source, attributeData) } ?: AttributeData()
    }

    override fun removeAttrData(entity: LivingEntity, source: String): AttributeData? {
        if (!entity.isAlive()) return null
        return attributeDataManager[entity.uuid]?.run {
            remove(source)
        }
    }

    override fun removeAttrData(uuid: UUID, source: String): AttributeData? {
        return uuid.validEntity()?.let { removeAttrData(it, source) }
    }


    override fun put(key: UUID, value: AttributeDataCompound): AttributeDataCompound? {
        return super.put(key, value)?.apply { entity = key.validEntity() }
    }
}