package dev.kamiql.database

import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import io.ktor.server.application.log

class DatabaseConfig {
    data class Provider(
        val clazz: Class<out Repository<*, *>>,
        val provide: (Repository<*, *>) -> Unit
    )

    val registry = mutableListOf<Repository<*, *>>()
    val providers = mutableListOf<Provider>()

    fun register(vararg repos: Repository<*, *>) {
        registry.addAll(repos)
        Repositories.registry.addAll(repos)
    }

    inline fun <reified T: Repository<*, *>> provider(noinline provide: (T) -> Unit) {
        providers.add(
            Provider(T::class.java) { repo ->
                @Suppress("UNCHECKED_CAST")
                provide(repo as T)
            }
        )
    }
}

val Database = createApplicationPlugin(
    "Database",
    createConfiguration = ::DatabaseConfig,
) {
    on(MonitoringEvent(ApplicationStarted)) { application ->
        pluginConfig.providers.forEach { provider ->
            pluginConfig.registry.filterIsInstance(provider.clazz).forEach { repo ->
                provider.provide(repo)
            }
        }
        pluginConfig.registry.forEach { it.load() }
        application.log.info("Loaded database repositories")
    }
    on(MonitoringEvent(ApplicationStopped)) { application ->
        pluginConfig.registry.forEach { it.close() }
        application.log.info("Shut down database repositories")
    }
}

object Repositories {
    val registry = mutableListOf<Repository<*, *>>()

    inline fun <reified T: Repository<*, *>> get(): T {
        return registry.filterIsInstance<T>().firstOrNull()
            ?: error("Repository ${T::class.simpleName} not registered")
    }
}
