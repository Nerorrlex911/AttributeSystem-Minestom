package com.github.zimablue.attrsystem.internal.core.attribute

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.api.Mapping
import com.github.zimablue.attrsystem.api.attribute.Attribute
import com.github.zimablue.attrsystem.api.read.ReadPattern
import com.github.zimablue.attrsystem.internal.core.attribute.mapping.DefaultMapping
import taboolib.common5.Coerce
import taboolib.library.configuration.ConfigurationSection

class ConfigAttributeBuilder(
    val key: String,
    val priority: Int,
    private val display: String? = null,
    private val names: List<String>,
    private val readPattern: ReadPattern<*>,
    private val isEntity: Boolean,
    private val mapping: Mapping?,
) {
    fun register() {
        AttributeSystem.attributeManager.register(
            Attribute.createAttribute(key, readPattern) {
                release = true
                this@ConfigAttributeBuilder.display?.let { display = it }
                priority = this@ConfigAttributeBuilder.priority
                entity = this@ConfigAttributeBuilder.isEntity
                names.addAll(this@ConfigAttributeBuilder.names)
                this@ConfigAttributeBuilder.mapping?.let { mapping = it }
            }
        )
    }

    companion object {
        @JvmStatic
        fun deserialize(section: ConfigurationSection): ConfigAttributeBuilder? {
            try {
                val attKey = section.name
                val priority = Coerce.toInteger(section["priority"].toString())
                val display = section["display"]?.toString()
                val names = if (section.contains("names")) section.getStringList("names") else listOf(attKey)
                val isEntity = (section["include-entity"]?.toString()?.lowercase() ?: "true") == "true"
                val readPatternKey =
                    section.getString("read-group")?.lowercase() ?: section.getString("read-pattern")?.lowercase()
                    ?: "default"
                val readPattern =
                    AttributeSystem.readPatternManager[readPatternKey]
                if (readPattern == null) {
                    AttributeSystem.logger.info(
                        "&d[&9AttributeSystem&d] &cThe ReadPattern &b{} &cof Attribute &6{} &cdoes not exist!",
                        attKey, readPatternKey)
                    return null
                }
                val map = section.getConfigurationSection("mapping")?.toMap()?.filterValues { it != null }
                val mapping = map?.let { DefaultMapping(it as Map<String,Any>) }
                return ConfigAttributeBuilder(attKey, priority, display, names, readPattern, isEntity, mapping)
            } catch (e: Throwable) {
                AttributeSystem.logger.error("error.attribute-load: {}", section["key"].toString())
                e.printStackTrace()
            }
            return null
        }
    }

    fun serialize(): MutableMap<String, Any> {
        return linkedMapOf(
            "priority" to priority,
            "names" to names,
            "read-group" to readPattern
        )
    }
}
