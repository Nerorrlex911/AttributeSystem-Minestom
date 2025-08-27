package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.api.AttributeSystemAPI
import com.github.zimablue.attrsystem.AttributeSystem
import com.skillw.attsystem.util.Utils.mirrorIfDebug
import com.skillw.pouvoir.util.isAlive
import net.minestom.server.entity.LivingEntity
import java.util.*

object AttributeSystemAPIImpl : AttributeSystemAPI {

    override fun update(entity: LivingEntity) {
        if (!entity.isAlive()) return
        mirrorIfDebug("update-entity") {
            mirrorIfDebug("update-equipment") {
                AttributeSystem.equipmentDataManager.update(entity)
            }
            mirrorIfDebug("update-attribute") {
                AttributeSystem.attributeDataManager.update(entity)
            }
            mirrorIfDebug("realize") {
                AttributeSystem.realizerManager.realize(entity)
            }
        }
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
