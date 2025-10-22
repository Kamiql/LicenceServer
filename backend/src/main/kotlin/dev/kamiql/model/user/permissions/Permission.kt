package dev.kamiql.model.user.permissions

enum class Permission(id: String) {
    ADMINISTRATOR("admin"),
    MANAGE_PERMISSIONS("manage_permissions"),
    BAN_USERS("ban_users");
}