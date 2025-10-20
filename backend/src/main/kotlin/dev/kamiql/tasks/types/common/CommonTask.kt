package dev.kamiql.tasks.types.common

import dev.kamiql.tasks.Task
import dev.kamiql.tasks.TaskScheduler
import java.util.concurrent.Future

class CommonTask(
    override val id: String,
    action: TaskScheduler<out Task>.() -> Unit
) : Task(action) {

    private var future: Future<*>? = null

    internal fun setFuture(f: Future<*>) {
        future = f
    }

    override fun cancel() {
        if (cancelled.compareAndSet(false, true)) {
            future?.cancel(true)
        }
    }
}