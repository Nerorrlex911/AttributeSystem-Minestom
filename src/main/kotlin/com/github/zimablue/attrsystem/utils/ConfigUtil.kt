package com.github.zimablue.attrsystem.utils

import com.github.zimablue.attrsystem.AttributeSystem

import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File

fun createIfNotExists(name: String, vararg fileNames: String) {
    val dir = File(name)
    if (!dir.exists()) {
        dir.mkdir()
        for (fileName in fileNames) {
            safe { AttributeSystem.savePackagedResource("$name/$fileName") }
        }
    }
}


inline fun <reified T> deserializeAll(dir: String, map: MutableMap<String,T>): MutableMap<String, T> {
    getAllFiles(File(AttributeSystem.dataDirectory.toFile(),dir))
        .map{ Configuration.loadFromFile(it, Type.YAML)}
        .flatMap { it.getKeys(false).map { key -> it.getConfigurationSection(key)!! }}
        .forEach {
            map[it.name] = Configuration.deserialize<T>(it, ignoreConstructor = true)
        }
    return map
}

/**
 * 获取文件夹内所有文件
 *
 * @param dir 待获取文件夹
 * @return 文件夹内所有文件
 */

fun getAllFiles(dir: File): ArrayList<File> {
    val list = ArrayList<File>()
    val files = dir.listFiles() ?: arrayOf<File>()
    for (file: File in files) {
        if (file.isDirectory) {
            list.addAll(getAllFiles(file))
        } else {
            list.add(file)
        }
    }
    return list
}