package dev.kamiql.model.user

import dev.kamiql.dto.DTOConvertible
import dev.kamiql.hash
import dev.kamiql.model.user.groups.Group
import dev.kamiql.model.session.UserSession
import dev.kamiql.util.data.DataStorage
import dev.kamiql.util.data.types.FileDataStorage
import dev.kamiql.util.id.types.UserSnowflake
import dev.kamiql.verify

data class User(
    override val id: UserSnowflake,
    override val username: String,
    override val email: String,
    val password: String,
    val avatar: String?, // path
    val groups: List<Group>,
) : UserLike, DTOConvertible<UserDTO>() {
    fun toSession(): UserSession {
        return UserSession(
            id,
            username,
            email,
        )
    }

    fun validatePassword(password: String): Boolean {
        return verify(password, this.password)
    }

    override fun toDTO(): UserDTO {
        return UserDTO(
            id = id.value,
            username = username,
            email = email,
            password = password,
            avatar = avatar?.let { path ->
                DataStorage.find<FileDataStorage>("avatars").run {
                    if (exists(path)) getUrl(path) else null
                }
            },
            groups = groups.map { it.id }
        )
    }

    companion object {
        fun new(session: UserSession, password: String): User {
            return User(
                session.id,
                session.username,
                session.email,
                hash(password),
                null,
                Group.default()
            )
        }
    }
}