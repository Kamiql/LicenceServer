package dev.kamiql.model.auth.discord

data class DiscordUser(
    val id: String,
    val username: String,
    val email: String?
)