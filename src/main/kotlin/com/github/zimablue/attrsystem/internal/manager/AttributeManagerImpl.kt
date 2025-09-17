package com.github.zimablue.attrsystem.internal.manager

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.attrsystem.api.attribute.Attribute
import com.github.zimablue.attrsystem.api.event.AttributeRegisterEvent
import com.github.zimablue.attrsystem.api.manager.AttributeManager
import com.github.zimablue.attrsystem.internal.core.attribute.ConfigAttributeBuilder
import com.github.zimablue.attrsystem.internal.manager.ASConfig.debug
import com.github.zimablue.attrsystem.utils.getAllFiles
import com.github.zimablue.attrsystem.utils.read.StrTrie
import com.github.zimablue.attrsystem.utils.safe
import com.github.zimablue.devoutserver.plugin.Plugin
import com.github.zimablue.devoutserver.plugin.lifecycle.AwakePriority
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import com.github.zimablue.devoutserver.util.map.BaseMap
import com.github.zimablue.devoutserver.util.map.LowerMap
import net.minestom.server.event.EventDispatcher
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

object AttributeManagerImpl: AttributeManager() {

    private val dataFolders = HashSet<File>()
    private val fileToKeys = BaseMap<File, HashSet<String>>()
    private val folderToKeys = BaseMap<File, HashSet<String>>()
    private val nameTrie = StrTrie<Attribute>()


    override val nameMap = LowerMap<Attribute>()

    override val attributes: MutableList<Attribute> by lazy {
        CopyOnWriteArrayList()
    }

    override fun find(text: String): Attribute? {
        return nameTrie.parse(text).result
    }

    override fun addSubPlugin(subPlugin: Plugin) {
        val folder = subPlugin.dataDirectory.toFile()
        addDataFolders(folder)
        subPlugin.lifeCycleManager.registerTask(PluginLifeCycle.RELOAD,AwakePriority.NORMAL) {
            reloadFolder(folder)
        }
    }

    fun onEnable() {
        addSubPlugin(AttributeSystem)
        onReload()
    }

    override fun reloadFolder(folder: File) {
        dataFolders.add(folder)
        folderToKeys[folder]?.forEach(::unregister)
        val listToFile = getAllFiles(File(AttributeSystem.dataDirectory.toFile(),"attributes"))
            .associate { Configuration.loadFromFile(it, Type.YAML) to it }
        val map = mutableMapOf<ConfigAttributeBuilder,File>()
        for ((list,file) in listToFile) {
            list.getKeys(false).forEach { key ->
                val builder = ConfigAttributeBuilder.deserialize(list.getConfigurationSection(key)!!)?:return@forEach
                map[builder] = file
                safe { builder.register() }
                if(fileToKeys.containsKey(file)) {
                    fileToKeys[file]!!.add(builder.key)
                } else {
                    fileToKeys[file] = HashSet<String>().apply { add(builder.key) }
                }
                if(folderToKeys.containsKey(file)) {
                    folderToKeys[file]!!.add(builder.key)
                } else {
                    folderToKeys[file] = HashSet<String>().apply { add(builder.key) }
                }

            }
        }
    }

    override fun addDataFolders(folder: File) {
        dataFolders.add(folder)
        onReload()
    }

    fun onReload() {
        this.entries.filter { it.value.config }.forEach { this.remove(it.key); }
        attributes.removeIf { it.config }
        this.nameMap.entries.filter { it.value.config }.forEach { nameMap.remove(it.key) }
    }

    override fun put(key: String, value: Attribute): Attribute? {
        attributes.removeIf { it.key == key }
        attributes.add(value)
        attributes.sort()

        nameMap[key] = value
        nameTrie.put(key, value)
        value.names.forEach {
            nameMap[it] = value
            nameTrie.put(it, value)
        }
        debug {
            AttributeSystem.logger.info(
                "[AttributeSystem] Registered Attribute: ${value.display} (Priority: ${value.priority})"
            )
        }
        EventDispatcher.call(AttributeRegisterEvent(value))
        return super.put(key, value)
    }

    override fun unregister(key: String) {
        remove(key)?.apply {
            names.forEach(nameMap::remove)
            debug {
                AttributeSystem.logger.info(
                    "[AttributeSystem] Unregistered Attribute: $display (Priority: ${priority})"
                )
            }
        }
    }

    override operator fun get(key: String): Attribute? {
        val lower = key.lowercase()
        return super.get(lower) ?: nameMap[lower]
    }
}