package dev.kamiql.model.user.groups

import dev.kamiql.model.user.permissions.Permission
import java.awt.Color

enum class Group(
    val id: String,
    val color: Int,
    val permissions: List<Permission>,
    val requires: List<String>
) {
    ADMINISTRATOR("admin", Color.RED.rgb, listOf(), listOf()),
    MODERATOR("moderator", Color.BLUE.rgb, listOf(), listOf()),
    USER("user", Color.GRAY.rgb, listOf(), listOf());

    fun hasPermission(perm: Permission): Boolean = permissions.contains(perm)
    fun canGrant(userGroups: List<Group>): Boolean = requires.all { it in userGroups.map { it.id } }

    companion object {
        fun default(): List<Group> {
            return listOf(USER)
        }
    }
}