package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem.attributeManager
import com.github.zimablue.attrsystem.AttributeSystem.compileManager
import com.github.zimablue.attrsystem.api.attribute.compound.AttributeData
import com.github.zimablue.attrsystem.api.compiled.CompiledData
import com.github.zimablue.attrsystem.api.compiled.sub.ComplexCompiledData
import com.github.zimablue.attrsystem.api.compiled.sub.NBTCompiledData
import com.github.zimablue.attrsystem.api.compiled.sub.StringsCompiledData
import com.github.zimablue.attrsystem.api.event.ItemReadEvent
import com.github.zimablue.attrsystem.api.event.StringsReadEvent
import com.github.zimablue.attrsystem.api.manager.ReadManager
import com.github.zimablue.attrsystem.utils.toMap
import com.github.zimablue.attrsystem.utils.toObj
import com.github.zimablue.devoutserver.util.map.BaseMap
import com.github.zimablue.devoutserver.util.uncolored
import net.kyori.adventure.nbt.BinaryTagTypes
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.ListBinaryTag
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.minestom.server.component.DataComponents
import net.minestom.server.entity.LivingEntity
import net.minestom.server.event.EventDispatcher
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag

object ReadManagerImpl : ReadManager() {
    private val lores = BaseMap<Int, List<String>>()

    override val ATTRIBUTE_DATA_TAG = Tag.NBT("ATTRIBUTE_DATA")
    override val CONDITION_DATA_TAG = Tag.NBT("CONDITION_DATA")

    private fun read(
        toRead: String,
        entity: LivingEntity?,
        slot: String?,
    ): AttributeData {
        val attributeData = AttributeData()
        val attribute = attributeManager.find(toRead) ?: return attributeData
        val read = attribute.readPattern
        val status = read.read(toRead, attribute, entity, slot) ?: return attributeData
        attributeData.operation(attribute, status)
        return attributeData
    }

    override fun read(
        strings: Collection<String>,
        entity: LivingEntity?,
        slot: String?,
    ): CompiledData? {

        val restStrings = ArrayList<String>()

        val compiledData = ComplexCompiledData()
        //��������
        for (string in strings) {
            var toRead = string
            if (ASConfig.ignores.any { toRead.uncolored().contains(it) }) continue
            val matcher = ASConfig.lineConditionPattern.matcher(toRead)
            if (!matcher.find()) {
                restStrings.add(toRead)
                continue
            }
            val builder: ((AttributeData) -> StringsCompiledData)? = runCatching {
                val requirements = matcher.group("requirement")
                val builders = requirements.split(ASConfig.lineConditionSeparator)
                    .mapNotNull { compileManager.compile(entity, it, slot) }
                return@runCatching { data: AttributeData ->
                    val lineConditions = StringsCompiledData(data)
                    builders.map { it(data) }.forEach(lineConditions::putAllCond)
                    lineConditions
                }
            }.getOrNull()
            if (builder == null) {
                restStrings.add(toRead)
                continue
            }
            toRead = matcher.replaceAll("")
            val attributeData = read(toRead, entity, slot)
            compiledData.add(builder.invoke(attributeData))
        }
        //������
        for (string in restStrings) {
            val attributeData = read(string, entity, slot)
            compileManager.compile(entity, string, slot)?.let {
                compiledData.putAll(it(attributeData))
            } ?: compiledData.addition.computeIfAbsent("STRINGS-ATTRIBUTE") { AttributeData() }
                .combine(attributeData)
        }
        val event = StringsReadEvent(entity, strings, compiledData)
        var result: CompiledData? = null
        EventDispatcher.callCancellable(event) {
            result = event.compiledData
        }
        return result

    }

    override fun readItemLore(
        itemStack: ItemStack,
        entity: LivingEntity?,
        slot: String?,
    ): CompiledData? {
        return if (itemStack.has(DataComponents.LORE)) {
            val origin = itemStack.get(DataComponents.LORE) ?: return null
            val lore = lores.computeIfAbsent(origin.hashCode()) {
                mutableListOf<Component>().apply { addAll(origin) }.map { PlainTextComponentSerializer.plainText().serialize(it) }
            }
            read(lore, entity, slot)

        } else null
    }

    override fun readItemsLore(
        itemStacks: Collection<ItemStack>,
        entity: LivingEntity?,
        slot: String?,
    ): CompiledData {
        val compiledData = ComplexCompiledData()
        for (item: ItemStack in itemStacks) {
            compiledData.add(
                readItemLore(item, entity, slot) ?: continue
            )
        }
        return compiledData
    }

    override fun readMap(
        attrDataMap: MutableMap<String, Any>,
        conditions: Collection<Any>,
        entity: LivingEntity?, slot: String?,
    ): NBTCompiledData {
        return compileManager.compile(entity, conditions, slot)(attrDataMap)
    }

    override fun readMap(map: Map<String, Any>, entity: LivingEntity?, slot: String?): CompiledData? {
        return when (map["type"].toString()) {
            "nbt" -> readMap(
                map["attributes"] as MutableMap<String, Any>,
                map["conditions"] as Collection<Any>,
                entity,
                slot
            )

            "strings" -> read(map["attributes"] as Collection<String>, entity, slot)

            else -> null
        }
    }

    override fun readItemNBT(
        itemStack: ItemStack,
        entity: LivingEntity?, slot: String?,
    ): CompiledData? {

        if (itemStack.isAir) return null

        val binaryTag = itemStack.getTag(ATTRIBUTE_DATA_TAG) ?: return null
        if (binaryTag.type() != BinaryTagTypes.COMPOUND) return null
        val attributeDataMap = toMap(binaryTag as CompoundBinaryTag)
        val listTag = itemStack.getTag(CONDITION_DATA_TAG)?:return null
        if (listTag.type() != BinaryTagTypes.LIST) return null
        val conditions = (listTag as ListBinaryTag).mapNotNull { toObj(it) }
        return readMap(attributeDataMap, conditions, entity, slot)

    }

    override fun readItemsNBT(
        itemStacks: Collection<ItemStack>,
        entity: LivingEntity?, slot: String?,
    ): CompiledData {

        val compiledData = ComplexCompiledData()
        for (item: ItemStack in itemStacks) {
            compiledData.add(
                readItemNBT(item, entity) ?: continue
            )
        }
        return compiledData

    }


    override fun readItem(
        itemStack: ItemStack,
        entity: LivingEntity?,
        slot: String?,
    ): CompiledData? {

        val compiledData = ComplexCompiledData()
        readItemLore(itemStack, entity, slot)?.let { compiledData.putAll(it) }
        readItemNBT(itemStack, entity, slot)?.let { compiledData.add(it) }

        val event = ItemReadEvent(
            entity,
            itemStack,
            compiledData,
            slot
        )
        var result: CompiledData? = null
        EventDispatcher.callCancellable(event) {
            result = event.compiledData
        }
        return result

    }


    override fun readItems(

        itemStacks: Collection<ItemStack>,
        entity: LivingEntity?,
        slot: String?,
    ): CompiledData {
        val compiledData = ComplexCompiledData()
        for (item: ItemStack in itemStacks) {
            readItem(item, entity, slot)?.let { compiledData.add(it) }
        }
        return compiledData
    }


}