package com.github.zimablue.attrsystem.utils

import net.kyori.adventure.nbt.*
import taboolib.library.configuration.ConfigurationSection

fun toNBT(obj: Any?) : BinaryTag {
    return when (obj) {
        null -> EndBinaryTag.endBinaryTag()
        is Byte -> ByteBinaryTag.byteBinaryTag(obj)
        is ByteArray -> ByteArrayBinaryTag.byteArrayBinaryTag(*obj)
        is Double -> DoubleBinaryTag.doubleBinaryTag(obj)
        is Float -> FloatBinaryTag.floatBinaryTag(obj)
        is Int -> IntBinaryTag.intBinaryTag(obj)
        is IntArray -> IntArrayBinaryTag.intArrayBinaryTag(*obj)
        is Long -> LongBinaryTag.longBinaryTag(obj)
        is LongArray -> LongArrayBinaryTag.longArrayBinaryTag(*obj)
        is Short -> ShortBinaryTag.shortBinaryTag(obj)
        is String -> StringBinaryTag.stringBinaryTag(obj)
        is List<*> -> translateList(obj)
        is ConfigurationSection -> translateSection(obj)
        is Map<*,*> -> CompoundBinaryTag.builder().apply { obj.forEach{k,v -> put(k.toString(),toNBT(v))} }.build()
        else -> throw IllegalArgumentException("Unsupported type: ${obj::class.java.name}")
    }
}
fun translateList(anyList: List<*>) : ListBinaryTag {
    val builder = ListBinaryTag.builder()
    for (item in anyList) {
        when (item) {
            is List<*> -> builder.add(translateList(item) as BinaryTag)
            is ConfigurationSection -> builder.add(translateSection(item))
            else -> builder.add(toNBT(item))
        }
    }
    return builder.build()
}
fun translateSection(section: ConfigurationSection) : CompoundBinaryTag {
    val builder = CompoundBinaryTag.builder()
    for (key in section.getKeys(false)) {
        when (val value = section[key]) {
            is List<*> -> builder.put(key, translateList(value))
            is ConfigurationSection -> builder.put(key, translateSection(value))
            else -> builder.put(key, toNBT(value))
        }
    }
    return builder.build()
}

fun toObj(tag: BinaryTag): Any? {
    when (tag.type()) {
        BinaryTagTypes.END -> return null
        BinaryTagTypes.BYTE -> return (tag as ByteBinaryTag).value()
        BinaryTagTypes.BYTE_ARRAY -> return (tag as ByteArrayBinaryTag).value()
        BinaryTagTypes.DOUBLE -> return (tag as DoubleBinaryTag).value()
        BinaryTagTypes.FLOAT -> return (tag as FloatBinaryTag).value()
        BinaryTagTypes.INT -> return (tag as IntBinaryTag).value()
        BinaryTagTypes.INT_ARRAY -> return (tag as IntArrayBinaryTag).value()
        BinaryTagTypes.LONG -> return (tag as LongBinaryTag).value()
        BinaryTagTypes.LONG_ARRAY -> return (tag as LongArrayBinaryTag).value()
        BinaryTagTypes.SHORT -> return (tag as ShortBinaryTag).value()
        BinaryTagTypes.STRING -> return (tag as StringBinaryTag).value()
        BinaryTagTypes.LIST -> return (tag as ListBinaryTag).map { toObj(it) }
        BinaryTagTypes.COMPOUND -> return toMap(tag as CompoundBinaryTag)
        else -> {
            throw IllegalArgumentException("Unsupported tag type: ${tag.type()}")
        }

    }
}
fun toMap(tag: CompoundBinaryTag): MutableMap<String, Any> {
    val map = mutableMapOf<String, Any>()
    tag.keySet().forEach { key ->
        val value = tag.get(key)?:return@forEach
        map[key] = toObj(value)?:return@forEach
    }
    return map
}
