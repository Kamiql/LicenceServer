package dev.kamiql

import dev.kamiql.database.Database
import dev.kamiql.security.auth.oauth.discord.model.OAuthState
import dev.kamiql.security.auth.oauth.discord.routes.discord
import dev.kamiql.security.sessions.model.UserSession
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level
import java.io.File
import java.util.*

val applicationHttpClient = HttpClient(CIO) {
    install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
        json(Json {
            prettyPrint = false
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::main).start(wait = true)
}

fun Application.main(http: HttpClient = applicationHttpClient) {
    install(CallLogging) {
        level = Level.DEBUG
        filter { true }
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = false
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    install(Koin) {
        slf4jLogger()
        modules(

        )
    }

    install(Authentication) {
        oauth("auth-oauth-discord") {
            urlProvider = { "http://localhost:8080/api/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "discord",
                    authorizeUrl = "https://discord.com/oauth2/authorize",
                    accessTokenUrl = "https://discord.com/api/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = "1429752318961778707",
                    clientSecret = "bGBQNyPDeBoU5Cx3f2LF0eCJFtt8Y8In",
                    defaultScopes = listOf("identify","email")
                )
            }
            client = http
        }
    }

    install(Sessions) {
        val secretEncryptKey = hex("00112233445566778899aabbccddeeff")
        val secretSignKey = hex("6819b57a326945c1968f45236589")
        cookie<UserSession>("USER_SESSION", directorySessionStorage(File("data/.sessions"))) {
            cookie.path = "/"
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
        cookie<OAuthState>("OAUTH_STATE")
    }

    install(Database) {
        register(

        )
    }

    routing {
        route("/api") {
            get("/login") {
                val state = UUID.randomUUID().toString()
                call.sessions.set(OAuthState(state))
                val redirectUrl = URLBuilder("https://discord.com/oauth2/authorize").apply {
                    parameters.append("response_type", "code")
                    parameters.append("client_id", "1429752318961778707")
                    parameters.append("redirect_uri", "http://localhost:8080/callback")
                    parameters.append("scope", "identify email")
                    parameters.append("state", state)
                }.buildString()
                call.respondRedirect(redirectUrl)
            }

            discord(http)
            // github(http)
        }
    }
}