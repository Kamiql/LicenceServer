package dev.kamiql.tasks.types.common

import dev.kamiql.tasks.TaskScheduler
import java.time.Duration
import java.util.concurrent.TimeUnit

class CommonScheduler : TaskScheduler<CommonTask>() {

    override fun runSync(block: TaskScheduler<CommonTask>.() -> Unit) {
        val task = CommonTask(id = generateId(), action = block)
        scheduledTasks += task
        val future = executor.submit { task.action(this) }
        task.setFuture(future)
    }

    override fun runSyncDelayed(delay: Duration, block: TaskScheduler<CommonTask>.() -> Unit) {
        val task = CommonTask(id = generateId(), action = block)
        scheduledTasks += task
        val future = executor.schedule({
            if (!task.isCancelled()) task.action(this)
        }, delay.toMillis(), TimeUnit.MILLISECONDS)
        task.setFuture(future)
    }

    override fun runSyncTimer(initialDelay: Duration, delay: Duration, block: TaskScheduler<CommonTask>.() -> Unit) {
        val task = CommonTask(id = generateId(), action = block)
        scheduledTasks += task
        val future = executor.scheduleAtFixedRate({
            if (!task.isCancelled()) task.action(this)
        }, initialDelay.toMillis(), delay.toMillis(), TimeUnit.MILLISECONDS)
        task.setFuture(future)
    }

    private fun generateId() = "task-${System.nanoTime()}"
}