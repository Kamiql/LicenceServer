package dev.kamiql.api.auth

import dev.kamiql.AuthType
import dev.kamiql.util.database.Repositories
import dev.kamiql.model.auth.discord.DiscordUser
import dev.kamiql.model.session.SessionStorage
import dev.kamiql.repository.user.UserRepository
import dev.kamiql.model.session.UserSession
import dev.kamiql.respondDTO
import dev.kamiql.session
import dev.kamiql.util.id.snowflake
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.auth.OAuthAccessTokenResponse
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.discord(http: HttpClient, redirects: MutableMap<String, String>) {
    authenticate(AuthType.OAUTH_DISCORD) {
        route("/discord") {
            get("/login") {}

            get("/callback") {
                val principal: OAuthAccessTokenResponse.OAuth2 = call.principal() ?: run {
                    call.respondRedirect("/home", true)
                    return@get
                }

                val state = principal.state ?: run {
                    call.respondRedirect("/home", true)
                    return@get
                }

                val discordUser: DiscordUser = http.get("https://discord.com/api/v10/users/@me") {
                    header(HttpHeaders.Authorization, "Bearer ${principal.accessToken}")
                }.body()

                val session = UserSession(
                    id = snowflake(discordUser.id),
                    username = discordUser.username,
                    email = discordUser.email
                )
                call.sessions.set(session)
                Repositories.get<UserRepository>().getOrCreate(session)

                val redirect = redirects[state] ?: run {
                    call.respondRedirect("http://localhost:5173/", true)
                    return@get
                }

                call.respondRedirect(redirect)
            }
        }
    }
}