package dev.kamiql.tasks

import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

abstract class TaskScheduler<T : Task> {
    protected val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(4)
    val scheduledTasks = mutableSetOf<T>()

    fun cancelAll() {
        scheduledTasks.forEach { it.cancel() }
        scheduledTasks.clear()
    }

    fun cancel(id: String) {
        val task = scheduledTasks.find { it.id == id } ?: return
        task.cancel()
        scheduledTasks.remove(task)
    }

    abstract fun runSync(block: TaskScheduler<T>.() -> Unit)
    abstract fun runSyncDelayed(delay: Duration, block: TaskScheduler<T>.() -> Unit)
    abstract fun runSyncTimer(initialDelay: Duration, delay: Duration, block: TaskScheduler<T>.() -> Unit)
}