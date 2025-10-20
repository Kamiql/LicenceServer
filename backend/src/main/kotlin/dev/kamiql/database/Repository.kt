package dev.kamiql.database

import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

/**
 * Base Repository
 * Provides common in-memory storage and CRUD operators.
 */
abstract class Repository<K: Any, V: Any>(
    open val kType: Type,
    open val vType: Type,
    open val debug: Boolean = false
): AutoCloseable {
    protected val data = ConcurrentHashMap<K, V>()

    abstract fun load()
    abstract fun shutdown()

    abstract fun save(key: K, value: V)
    abstract fun delete(key: K)

    operator fun minus(key: K) {
        data.remove(key)
        delete(key)
    }

    operator fun invoke(key: K): V? = data[key]
    operator fun get(key: K): V? = data[key]

    operator fun set(key: K, value: V) {
        data[key] = value
        save(key, value)
    }

    operator fun contains(key: K): Boolean = data.containsKey(key)

    fun getOrDefault(key: K, default: () -> V): V = data.getOrPut(key) {
        val value = default()
        save(key, value)
        value
    }

    fun getOrDefault(key: K, default: V): V = data.getOrPut(key) {
        save(key, default)
        default
    }

    fun mutate(key: K, mutation: (V) -> V) {
        val newValue = mutation(data[key] ?: return)
        data[key] = newValue
        save(key, newValue)
    }

    abstract fun debug(message: String)
}