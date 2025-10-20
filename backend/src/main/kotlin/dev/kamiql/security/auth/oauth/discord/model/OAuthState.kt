package dev.kamiql.security.auth.oauth.discord.model

import kotlinx.serialization.Serializable

@Serializable
class OAuthState(val state: String)