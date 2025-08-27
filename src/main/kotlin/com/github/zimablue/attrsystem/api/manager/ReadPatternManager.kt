package com.github.zimablue.attrsystem.api.manager

import com.github.zimablue.attrsystem.api.read.ReadPattern
import com.github.zimablue.devoutserver.util.map.LowerKeyMap

abstract class ReadPatternManager: LowerKeyMap<ReadPattern<*>>() {
}