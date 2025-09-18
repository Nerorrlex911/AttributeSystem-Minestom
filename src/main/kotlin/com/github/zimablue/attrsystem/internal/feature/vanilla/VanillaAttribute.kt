package com.github.zimablue.attrsystem.internal.feature.vanilla

import com.github.zimablue.attrsystem.internal.feature.calc.FormulaParser
import com.github.zimablue.attrsystem.internal.manager.VanillaAttributeManager
import com.github.zimablue.attrsystem.internal.manager.VanillaAttributeManager.vanilla
import com.github.zimablue.devoutserver.util.map.component.Registrable
import com.github.zimablue.pouplaceholder.PouPlaceholder
import net.kyori.adventure.key.Key
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.entity.attribute.AttributeModifier
import net.minestom.server.entity.attribute.AttributeOperation
import net.minestom.server.tag.Tag

class VanillaAttribute(
    override val key: String,
) : Registrable<String> {
    val enable: Boolean
        get() = vanilla.getBoolean("$key.enable", true)
    val value: String
        get() = vanilla.getString("$key.value", "0")!!
    private val attr: Attribute = Attribute.fromKey(key)?: throw IllegalArgumentException("Invalid vanilla attribute name: $key")
    private val attrKey = Key.key("AS-${attr.name()}")
    private val cacheTag = Tag.Double("attrsystem.previous.${attr.name()}")
    fun getValue(entity: LivingEntity): Double {
        val attrValue = FormulaParser.calculate(PouPlaceholder.placeholderManager.replace(entity, value))
        return attrValue
    }

    /**
     * check if attribute was changed
     */
    private fun isChanged(entity: LivingEntity, value: Double): Boolean {
        val current = entity.getTag(cacheTag)
        if(current != value) {
            entity.setTag(cacheTag, value)
            return true
        } else {
            return false
        }
    }
    fun update(entity: LivingEntity) {
        val newValue = getValue(entity)
        //check if attribute was changed, if not, don't update
        if(!isChanged(entity, newValue)) return
        val attrInstance = entity.getAttribute(attr)
        attrInstance.removeModifier(attrKey)
        attrInstance.addModifier(
            AttributeModifier(attrKey, newValue, AttributeOperation.ADD_VALUE)
        )
    }

    override fun register() {
        VanillaAttributeManager.register(this)
    }
}