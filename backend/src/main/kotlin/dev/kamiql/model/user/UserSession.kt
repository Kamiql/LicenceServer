package dev.kamiql.model.user

import dev.kamiql.database.Repositories
import dev.kamiql.repository.user.UserRepository
import dev.kamiql.util.id.types.UserSnowflake

data class UserSession(
    override val id: UserSnowflake,
    override val username: String,
    override val email: String?
) : UserLike {
    fun toUser(): User? {
        return Repositories.get<UserRepository>()[id.value]?.toModel()
    }
}