package dev.kamiql.repository.user

import com.google.gson.reflect.TypeToken
import dev.kamiql.util.database.types.MongoRepository
import dev.kamiql.model.user.User
import dev.kamiql.model.user.UserDTO
import dev.kamiql.model.session.UserSession
import dev.kamiql.util.id.types.UserSnowflake

class UserRepository : MongoRepository<Long, UserDTO>("users", Long::class.java, object : TypeToken<UserDTO>(){}.type) {
    fun findByName(name: String): UserDTO? {
        return this.values().find { it.username.equals(name, true) }
    }

    fun findByEmail(email: String): UserDTO? {
        return this.values().find { it.email.equals(email, true) }
    }
}