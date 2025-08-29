package com.github.zimablue.attrsystem.internal.core.schedule

import com.github.zimablue.attrsystem.AttributeSystem
import kotlinx.coroutines.*

object TaskScheduler {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun schedule(
        name: String,
        intervalMillis: Long,
        task: suspend () -> Unit
    ) : Job {
        return scope.launch {
            while (isActive) {
                try {
                    task()
                } catch (e: CancellationException) {
                    break
                } catch (e: Exception) {
                    AttributeSystem.logger.error("Error occurred in scheduled task '$name': ${e.message}", e)
                }
                delay(intervalMillis)
            }
        }
    }
    fun scheduleOnce(name: String, task: suspend () -> Unit) : Job {
        return scope.launch {
            try {
                task()
            } catch (e: CancellationException) {
                AttributeSystem.logger.error("CancellationException occurred in scheduled task '$name': ${e.message}", e)
            } catch (e: Exception) {
                AttributeSystem.logger.error("Error occurred in scheduled task '$name': ${e.message}", e)
            }
        }
    }

    fun stopAll() {
        scope.cancel()
    }
}