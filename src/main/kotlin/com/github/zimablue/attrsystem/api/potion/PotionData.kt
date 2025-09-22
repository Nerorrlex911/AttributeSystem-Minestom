package com.github.zimablue.attrsystem.api.potion

import net.minestom.server.timer.Task

class PotionData(
    val data: Map<String,Any>,
    val duration: Long=-1,
    val persistent: Boolean=false,
    val removeOnDeath: Boolean=true,
    @Transient var task: Task?=null,
) {
    @Transient val end = System.currentTimeMillis()+duration
    fun release() {
        task?.cancel()
    }
    fun resetDuration() : PotionData {
        val durationLeft = if(duration==-1L) duration else end - duration
        return PotionData(data, durationLeft, persistent, removeOnDeath)
    }
}