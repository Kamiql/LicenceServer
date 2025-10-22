package dev.kamiql

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import dev.kamiql.database.Database
import dev.kamiql.database.types.MongoRepository
import dev.kamiql.api.auth.login
import dev.kamiql.model.auth.discord.OAuthState
import dev.kamiql.api.auth.discord.discord
import dev.kamiql.api.user.user
import dev.kamiql.repository.user.UserRepository
import dev.kamiql.model.user.UserSession
import dev.kamiql.util.data.cdnRoute
import dev.kamiql.util.data.types.FileDataStorage
import dev.kamiql.util.tasks.TaskScheduler
import dev.kamiql.util.tasks.types.CommonScheduler
import dev.kamiql.util.gson.GsonUtil
import dev.kamiql.util.gson.gson
import dev.kamiql.util.gson.serializers.GsonSessionSerializer
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import org.bson.UuidRepresentation
import org.litote.kmongo.KMongo
import org.slf4j.event.Level
import java.io.File

val taskScheduler: TaskScheduler = CommonScheduler()

val applicationHttpClient = HttpClient(CIO) {
    install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
        gson(ContentType.Application.Json, GsonUtil.gson())
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::main).start(wait = true)
}

fun Application.main(http: HttpClient = applicationHttpClient) {
    install(CallLogging) {
        level = Level.INFO
        filter { true }
    }

    install(ContentNegotiation) {
        gson(ContentType.Application.Json, GsonUtil.gson())
    }

    install(Sessions) {
        val secretEncryptKey = hex("78777f03ea555e28c1f1693d2893c964")
        val secretSignKey = hex("34a95b68a9f8f5b060450e362e0dfa0d")
        cookie<UserSession>("USER_SESSION", directorySessionStorage(File("data/.sessions"))) {
            serializer = GsonSessionSerializer(UserSession::class.java)
            cookie.path = "/"
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
        cookie<OAuthState>("OAUTH_STATE") {
            serializer = GsonSessionSerializer(OAuthState::class.java)
        }
    }

    install(Authentication) {
        oauth(AuthType.OAUTH_DISCORD) {
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
            skipWhen { call -> call.sessions.get<UserSession>().isNotNull() }
        }

        session<UserSession>(AuthType.SESSION) {
            validate { session ->
                val res = session.toUser().isNotNull()
                println("Validating session for ${session.username} -> $res")
                res
            }
            challenge {
                println("Redirect to login")
                call.respondRedirect("/app/login", true)
            }
        }
    }

    install(Database) {
        val db = let {
            val settings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyConnectionString(ConnectionString(
                    System.getenv("MONGO_URI")
                ))
                .build()
            KMongo.createClient(settings)
        }.getDatabase("app")

        register(
            UserRepository()
        )

        provider<MongoRepository<*, *>> {
            it.db = db
            it.taskScheduler = taskScheduler
        }
    }

    routing {
        route("/api") {
            login()
            discord(http)
            user()
        }

        cdnRoute<FileDataStorage>(FileDataStorage("avatars",
            "png",
            "jpg",
            "jpeg",
            "svg",
            "gif",
            "webp"
        ))
    }
}