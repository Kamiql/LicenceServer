package dev.kamiql.model.user.groups

import java.awt.Color

enum class Group(
    val id: String,
    val color: Int,
    val requires: List<String>,
    val childs: List<Group> = listOf()
) {
    USER("user", Color.GRAY.rgb, listOf()),
    MODERATOR("moderator", Color.BLUE.rgb, listOf(), listOf(USER)),
    ADMINISTRATOR("admin", Color.RED.rgb, listOf(), listOf(MODERATOR)),
    OWNER("owner", Color.ORANGE.rgb, listOf(), listOf(USER, MODERATOR, ADMINISTRATOR));

    fun canGrant(userGroups: List<Group>): Boolean = requires.all { it in userGroups.map { it.id } }

    fun hasOrInherits(group: Group): Boolean {
        if (this == OWNER) return true
        if (this == group) return true
        return childs.any { it.hasOrInherits(group) }
    }

    companion object {
        fun default(): List<Group> = listOf(USER)
    }
}
