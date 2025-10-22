package dev.kamiql.api.auth

import dev.kamiql.model.auth.discord.OAuthState
import io.ktor.http.URLBuilder
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import java.util.UUID

fun Route.login() {
    get("/login") {
        val state = UUID.randomUUID().toString()
        call.sessions.set(OAuthState(state))
        val redirectUrl = URLBuilder("https://discord.com/oauth2/authorize").apply {
            parameters.append("response_type", "code")
            parameters.append("client_id", "1429752318961778707")
            parameters.append("redirect_uri", "http://localhost:8080/api/callback")
            parameters.append("scope", "identify email")
            parameters.append("state", state)
        }.buildString()
        call.respondRedirect(redirectUrl)
    }
}