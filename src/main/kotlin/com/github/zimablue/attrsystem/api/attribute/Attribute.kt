package com.github.zimablue.attrsystem.api.attribute

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.api.Mapping
import com.github.zimablue.attrsystem.api.read.ReadPattern
import com.github.zimablue.devoutserver.util.map.component.Registrable

/**
 * Attribute
 *
 * @constructor Create Attribute
 * @property key 键
 * @property names 名称
 * @property readPattern 读取格式
 * @property priority 优先级
 */
class Attribute private constructor(
    override val key: String,
    val display: String,
    val names: Collection<String>,
    val readPattern: ReadPattern<*>,
    val priority: Int = 0,
) : Registrable<String>, Comparable<Attribute> {
    override fun compareTo(other: Attribute): Int = if (this.priority == other.priority) 0
    else if (this.priority > other.priority) 1
    else -1

    /** Entity */
    var entity = true

    /** Release */
    var config = false

    override fun register() {
        AttributeSystem.attributeManager.register(this)
    }

    var mapping: Mapping? = null
        set(value) {
            value?.attribute = this
            field = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Attribute

        return key == other.key
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

    override fun toString(): String {
        return "Attribute(key='$key')"
    }

    /**
     * Builder
     *
     * @param receiver
     * @constructor
     * @property key 键
     * @property readPattern 读取格式
     */
    class Builder(val key: String, private val readPattern: ReadPattern<*>, receiver: Builder.() -> Unit) {
        var display: String? = null

        /** Entity */
        var entity = true

        /** Release */
        var release = false

        /** Priority */
        var priority: Int = 0

        /** Names */
        val names = ArrayList<String>()

        var mapping: Mapping? = null


        init {
            receiver.invoke(this)
        }

        /**
         * Build
         *
         * @return
         */
        fun build(): Attribute {
            val att = Attribute(key, display ?: names.first(), names, readPattern, priority)
            att.config = release
            att.entity = entity
            att.mapping = mapping
            return att
        }

    }

    companion object {
        @JvmStatic
        fun createAttribute(
            key: String,
            readPattern: ReadPattern<*>,
            init: Builder.() -> Unit,
        ): Attribute {
            return Builder(key, readPattern, init).build()
        }
    }
}
