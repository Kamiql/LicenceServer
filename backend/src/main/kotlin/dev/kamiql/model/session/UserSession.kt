package dev.kamiql.model.session

import dev.kamiql.util.database.Repositories
import dev.kamiql.model.user.User
import dev.kamiql.model.user.UserLike
import dev.kamiql.repository.user.UserRepository
import dev.kamiql.util.id.types.UserSnowflake

data class UserSession(
    override val id: UserSnowflake,
    override val username: String,
    override val email: String?
) : UserLike, Session {
    fun toUser(): User {
        return Repositories.get<UserRepository>().getOrCreate(this)
    }
}