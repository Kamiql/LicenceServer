package dev.kamiql.util.tasks

import java.time.Duration

abstract class TaskScheduler {
    val scheduledTasks = mutableSetOf<Task>()

    fun cancelAll() {
        scheduledTasks.forEach { it.cancel() }
        scheduledTasks.clear()
    }

    fun cancel(id: String) {
        val task = scheduledTasks.find { it.id == id } ?: return
        task.cancel()
        scheduledTasks.remove(task)
    }

    abstract fun runSync(block: () -> Unit)
    abstract fun runSyncDelayed(delay: Duration, block: () -> Unit)
    abstract fun runSyncTimer(initialDelay: Duration, delay: Duration, block: () -> Unit)

    abstract fun runAsync(block: () -> Unit)
    abstract fun runAsyncDelayed(delay: Duration, block: () -> Unit)
    abstract fun runAsyncTimer(initialDelay: Duration, delay: Duration, block: () -> Unit)
}