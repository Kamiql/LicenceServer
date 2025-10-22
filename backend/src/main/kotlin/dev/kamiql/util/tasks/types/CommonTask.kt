package dev.kamiql.util.tasks.types

import dev.kamiql.util.tasks.Task
import java.util.concurrent.Future

class CommonTask(
    override val id: String,
    action: () -> Unit
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