package com.github.zimablue.attrsystem.api.event

import net.minestom.server.event.Event
import com.github.zimablue.attrsystem.api.attribute.Attribute
class AttributeRegisterEvent(val attribute: Attribute) : Event {
}