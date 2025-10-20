package dev.kamiql.security.identity.model

import dev.kamiql.security.identity.groups.Group
import dev.kamiql.util.id.types.UserSnowflake
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @Serializable(with = UserSnowflake.Serializer::class)
    override val id: UserSnowflake,
    override val username: String,
    override val email: String?,
    val avatar: String?,
    val groups: List<Group>
) : UserLike {
    fun createIfAbsent() {

    }
}