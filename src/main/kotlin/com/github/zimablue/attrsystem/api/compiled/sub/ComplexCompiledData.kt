package com.github.zimablue.attrsystem.api.compiled.sub

import com.github.zimablue.attrsystem.api.attribute.compound.AttributeDataCompound
import com.github.zimablue.attrsystem.api.compiled.CompiledData
import net.minestom.server.entity.LivingEntity

/**
 * @className ComplexCompiledData
 *
 * @author Glom
 * @date 2023/8/2 21:25 Copyright 2023 user. All rights reserved.
 */
class ComplexCompiledData : CompiledData() {
    private val children = ArrayList<CompiledData>()
    var base: CompiledData? = null
    val addition = AttributeDataCompound()
    fun add(compiled: CompiledData) {
        if (compiled is NBTCompiledData) {
            putAllCond(compiled)
            compiled.clear()
        }
        children.add(compiled)
    }

    fun layers(current: Int): Int {
        return children.filterIsInstance<ComplexCompiledData>().maxOfOrNull { it.layers(current + 1) } ?: current
    }

    override fun putAll(other: CompiledData) {
        super.putAll(other)
        if (other is ComplexCompiledData)
            combine(other)
    }

    fun combine(other: ComplexCompiledData) {
        addition.combine(other.addition)
        children.addAll(other.children)
    }

    override fun clear() {
        super.clear()
        clearChildren()
        addition.clear()
    }

    fun clearChildren() {
        children.clear()
    }

    override fun eval(entity: LivingEntity?): AttributeDataCompound {
        if (!condition(entity)) return AttributeDataCompound()
        val total = AttributeDataCompound(entity)
        base?.eval(entity)?.let { total.combine(it) }
        if (addition.isNotEmpty()) {
            total.combine(addition.clone())
        }
        children.forEach {
            total.combine(it.eval(entity))
        }
        return total
    }

    override fun serialize(): MutableMap<String, Any> {
        val total = super.serialize()

        val children = LinkedHashMap<String, Any>()
        this.children.forEach {
            children.putAll(it.serialize())
        }
        return linkedMapOf(
            "ComplexCompiledData-${hashCode()}" to linkedMapOf(
                "conditions" to total,
                "base" to base?.serialize(),
                "addition" to addition.serialize(),
                "children" to children,
            )
        )
    }

    override fun hashCode(): Int {
        return super.hashCode() + children.hashCode() + addition.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComplexCompiledData) return false
        if (!super.equals(other)) return false

        if (children != other.children) return false
        return addition == other.addition
    }
}