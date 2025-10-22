package dev.kamiql.util.tasks.types

import dev.kamiql.util.tasks.TaskScheduler
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class CommonScheduler : TaskScheduler() {
    private val syncExecutor: ScheduledExecutorService = Executors.newScheduledThreadPool(4)
    private val asyncExecutor: ScheduledExecutorService = Executors.newScheduledThreadPool(8)

    override fun runSync(block: () -> Unit) {
        val task = CommonTask(id = generateId(), action = block)
        scheduledTasks += task
        val future = syncExecutor.submit { task.action() }
        task.setFuture(future)
    }

    override fun runSyncDelayed(delay: Duration, block: () -> Unit) {
        val task = CommonTask(id = generateId(), action = block)
        scheduledTasks += task
        val future = syncExecutor.schedule({
            if (!task.isCancelled()) task.action()
        }, delay.toMillis(), TimeUnit.MILLISECONDS)
        task.setFuture(future)
    }

    override fun runSyncTimer(initialDelay: Duration, delay: Duration, block: () -> Unit) {
        val task = CommonTask(id = generateId(), action = block)
        scheduledTasks += task
        val future = syncExecutor.scheduleAtFixedRate({
            if (!task.isCancelled()) task.action()
        }, initialDelay.toMillis(), delay.toMillis(), TimeUnit.MILLISECONDS)
        task.setFuture(future)
    }

    override fun runAsync(block: () -> Unit) {
        val task = CommonTask(id = generateId(), action = block)
        scheduledTasks += task
        val future = asyncExecutor.submit { task.action() }
        task.setFuture(future)
    }

    override fun runAsyncDelayed(delay: Duration, block: () -> Unit) {
        val task = CommonTask(id = generateId(), action = block)
        scheduledTasks += task
        val future = asyncExecutor.schedule({
            if (!task.isCancelled()) task.action()
        }, delay.toMillis(), TimeUnit.MILLISECONDS)
        task.setFuture(future)
    }

    override fun runAsyncTimer(initialDelay: Duration, delay: Duration, block: () -> Unit) {
        val task = CommonTask(id = generateId(), action = block)
        scheduledTasks += task
        val future = asyncExecutor.scheduleAtFixedRate({
            if (!task.isCancelled()) task.action()
        }, initialDelay.toMillis(), delay.toMillis(), TimeUnit.MILLISECONDS)
        task.setFuture(future)
    }

    private fun generateId() = "task-${System.nanoTime()}"
}
