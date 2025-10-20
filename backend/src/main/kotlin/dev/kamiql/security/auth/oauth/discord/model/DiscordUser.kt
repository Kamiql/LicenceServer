package dev.kamiql.security.auth.oauth.discord.model

import kotlinx.serialization.Serializable

@Serializable
data class DiscordUser(
    val id: String,
    val username: String,
    val email: String?
)