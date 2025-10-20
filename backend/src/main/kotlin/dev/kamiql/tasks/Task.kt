package dev.kamiql.tasks

import java.util.concurrent.atomic.AtomicBoolean

abstract class Task(
    val action: TaskScheduler<Task>.() -> Unit
) {
    abstract val id: String
    protected val cancelled = AtomicBoolean(false)
    abstract fun cancel()
    fun isCancelled(): Boolean = cancelled.get()
}