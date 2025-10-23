package dev.kamiql.model.user

import dev.kamiql.dto.DTO
import dev.kamiql.model.user.groups.Group
import dev.kamiql.util.data.DataStorage
import dev.kamiql.util.data.types.FileDataStorage
import dev.kamiql.util.gson.toJson
import dev.kamiql.util.id.snowflake

class UserDTO(
    val id: Long,
    val username: String,
    val email: String?,
    val avatar: String?, // url
    val groups: List<String>
) : DTO<User>() {
    override fun toModel(): User {
        return User(
            snowflake(id),
            username,
            email,
            avatar?.let { url ->
                DataStorage.find<FileDataStorage>("avatars").run {
                    if (exists(url)) fromUrl(url) else null
                }
            },
            groups.map { id -> Group.entries.first { it.id == id } }
        )
    }

    override fun toString(): String = this.toJson()
}