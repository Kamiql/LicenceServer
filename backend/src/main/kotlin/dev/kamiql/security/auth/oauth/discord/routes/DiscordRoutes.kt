package dev.kamiql.security.auth.oauth.discord.routes

import dev.kamiql.security.auth.oauth.discord.model.DiscordUser
import dev.kamiql.security.auth.oauth.discord.model.OAuthState
import dev.kamiql.security.auth.oauth.discord.model.OAuthTokenResponse
import dev.kamiql.security.sessions.model.UserSession
import dev.kamiql.util.id.snowflake
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.discord(http: HttpClient) {
    get("/callback") {
        val code = call.parameters["code"] ?: return@get call.respondRedirect("/login")
        val state = call.parameters["state"] ?: return@get call.respondRedirect("/login")

        val sessionState = call.sessions.get<OAuthState>()?.state
        if (state != sessionState) return@get call.respondText("Invalid state")

        val tokenResponse: OAuthTokenResponse = http.submitForm(
            url = "https://discord.com/api/oauth2/token",
            formParameters = Parameters.build {
                append("client_id", "1429752318961778707")
                append("client_secret", "bGBQNyPDeBoU5Cx3f2LF0eCJFtt8Y8In")
                append("grant_type", "authorization_code")
                append("code", code)
                append("redirect_uri", "http://localhost:8080/callback")
            },
        ) {
            header(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
        }.body()

        val discordUser: DiscordUser = http.get("https://discord.com/api/v10/users/@me") {
            header(HttpHeaders.Authorization, "Bearer ${tokenResponse.access_token}")
        }.body()

        call.sessions.set(UserSession(
            id = snowflake(discordUser.id),
            username = discordUser.username,
            email = discordUser.email
        ))

        call.respondRedirect("/app/home")
    }
}