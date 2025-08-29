package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.api.AttributeSystemAPI
import com.github.zimablue.attrsystem.utils.isAlive
import net.minestom.server.entity.LivingEntity
import java.util.*

object AttributeSystemAPIImpl : AttributeSystemAPI {

    override fun update(entity: LivingEntity) {
        if (!entity.isAlive()) return

            AttributeSystem.equipmentDataManager.update(entity)

            AttributeSystem.attributeDataManager.update(entity)

    }


    override fun remove(entity: LivingEntity) {
        this.remove(entity.uuid)
    }


    override fun remove(uuid: UUID) {
        AttributeSystem.attributeDataManager.remove(uuid)
        AttributeSystem.equipmentDataManager.remove(uuid)
        AttributeSystem.compiledAttrDataManager.remove(uuid)
    }

}
