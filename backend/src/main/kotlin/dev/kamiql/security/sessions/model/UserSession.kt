package dev.kamiql.security.sessions.model

import dev.kamiql.security.identity.model.UserLike
import dev.kamiql.util.id.types.UserSnowflake
import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    @Serializable(with = UserSnowflake.Serializer::class)
    override val id: UserSnowflake,
    override val username: String,
    override val email: String?
) : UserLike