package dev.kamiql.model.session

import dev.kamiql.util.database.types.MongoRepository
import io.ktor.server.sessions.SessionStorage

class SessionStorage : MongoRepository<String, String>("sessions", String::class.java, String::class.java), SessionStorage {

    override suspend fun write(id: String, value: String) {
        this[id] = value
    }

    override suspend fun invalidate(id: String) {
        delete(id)
    }

    override suspend fun read(id: String): String {
        return this[id] ?: throw NoSuchElementException("Session $id not found")
    }
}