package dev.kamiql.database

import dev.kamiql.database.types.MongoRepository
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import io.ktor.server.application.log

class DatabaseConfig() {
    internal val registry = mutableListOf<Repository<*, *>>()

    fun register(vararg repos: Repository<*, *>) {
        registry.addAll(repos)
    }
}

val Database = createApplicationPlugin(
    "Database",
    createConfiguration = ::DatabaseConfig,
) {
    on(MonitoringEvent(ApplicationStarted)) { application ->
        pluginConfig.registry.filterIsInstance<MongoRepository>()
        pluginConfig.registry.forEach { it.load() }
        application.log.info("Loaded database repositories")
    }
    on(MonitoringEvent(ApplicationStopped)) { application ->
        pluginConfig.registry.forEach { it.shutdown() }
        application.log.info("Shut down database repositories")
    }
}