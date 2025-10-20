package dev.kamiql.security.identity.model

import dev.kamiql.util.id.types.UserSnowflake

interface UserLike {
    val id: UserSnowflake
    val username: String
    val email: String?
}