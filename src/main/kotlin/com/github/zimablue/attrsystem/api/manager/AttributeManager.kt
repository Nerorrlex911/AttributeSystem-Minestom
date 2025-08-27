package com.github.zimablue.attrsystem.api.manager

import com.github.zimablue.attrsystem.api.attribute.Attribute
import com.github.zimablue.devoutserver.plugin.Plugin
import com.github.zimablue.devoutserver.util.map.LowerKeyMap
import com.github.zimablue.devoutserver.util.map.LowerMap
import java.io.File

abstract class AttributeManager : LowerKeyMap<Attribute>() {


    abstract val nameMap: LowerMap<Attribute>

    /** Attributes （按权重排列） */
    abstract val attributes: List<Attribute>
    abstract fun reloadFolder(folder: File)
    abstract fun addDataFolders(folder: File)
    abstract fun addSubPlugin(subPlugin: Plugin)
    abstract fun unregister(key: String)

    abstract fun find(text: String): Attribute?
}