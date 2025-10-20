package dev.kamiql.security.identity.permissions

enum class Permission(id: String) {
    ADMINISTRATOR("admin"),
    MANAGE_PERMISSIONS("manage_permissions"),
    BAN_USERS("ban_users");
}