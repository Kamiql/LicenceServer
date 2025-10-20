package dev.kamiql.security.auth.oauth.discord.model

import kotlinx.serialization.Serializable

@Serializable
data class OAuthTokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val refresh_token: String? = null,
    val scope: String
)