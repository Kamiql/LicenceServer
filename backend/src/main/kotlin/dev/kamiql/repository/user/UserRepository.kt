package dev.kamiql.repository.user

import com.google.gson.reflect.TypeToken
import dev.kamiql.database.types.MongoRepository
import dev.kamiql.model.user.User
import dev.kamiql.model.user.UserDTO
import dev.kamiql.model.user.UserSession
import dev.kamiql.util.id.types.UserSnowflake

class UserRepository : MongoRepository<Long, UserDTO>("users", UserSnowflake::class.java, object : TypeToken<User>(){}.type) {
    fun getOrCreate(session: UserSession): User {
        return mutate(session.id.value) { it ?: User.new(session).toDTO() }.toModel()
    }
}