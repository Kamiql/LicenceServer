package dev.kamiql

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import dev.kamiql.api.auth.basicAuth
import dev.kamiql.util.database.Database
import dev.kamiql.util.database.types.MongoRepository
import dev.kamiql.api.cdn.cdnRoute
import dev.kamiql.api.user.user
import dev.kamiql.util.database.Repositories
import dev.kamiql.model.session.SessionStorage
import dev.kamiql.repository.user.UserRepository
import dev.kamiql.model.session.UserSession
import dev.kamiql.model.user.User
import dev.kamiql.model.verification.PendingVerification
import dev.kamiql.repository.verification.VerificationRepository
import dev.kamiql.util.data.types.FileDataStorage
import dev.kamiql.util.tasks.TaskScheduler
import dev.kamiql.util.tasks.types.CommonScheduler
import dev.kamiql.util.gson.GsonUtil
import dev.kamiql.util.gson.gson
import dev.kamiql.util.gson.serializers.GsonSessionSerializer
import dev.kamiql.util.id.newSnowflake
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.hex
import org.bson.UuidRepresentation
import org.litote.kmongo.KMongo
import org.slf4j.event.Level

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

    install(Database) {
        val db = let {
            val settings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyConnectionString(ConnectionString(
                    "mongodb://root:LCKyMmVzy517t4bV@localhost:27017/"//System.getenv("MONGO_URI")
                ))
                .build()
            KMongo.createClient(settings)
        }.getDatabase("app")

        register(
            UserRepository(),
            SessionStorage(),
            VerificationRepository()
        )

        provider<MongoRepository<*, *>> {
            it.db = db
            it.taskScheduler = taskScheduler
        }
    }

    install(Sessions) {
        val secretEncryptKey = hex("78777f03ea555e28c1f1693d2893c964")
        val secretSignKey = hex("34a95b68a9f8f5b060450e362e0dfa0d")
        cookie<UserSession>("USER_SESSION", Repositories.get<SessionStorage>()) {
            cookie.path = "/"
            serializer = GsonSessionSerializer(UserSession::class.java)
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
    }

    install(Authentication) {

    }

    routing {
        route("/api") {
            route("/auth") {
                basicAuth()

                get("/logout") {
                    call.sessionId?.let { Repositories.get<SessionStorage>().invalidate(it) }
                    call.sessions.clear<UserSession>()
                    return@get call.respond(HttpStatusCode.OK)
                }

                get("/me") {
                    session { user ->
                        call.respondDTO(user.toDTO())
                    }
                }

                post("/verify") {
                    data class VerifyRequest(val id: String)

                    val request = call.receive<VerifyRequest>()
                    val pending = Repositories.get<VerificationRepository>()[request.id]
                        ?: return@post call.respond(HttpStatusCode.NotFound, "Invalid verification token")

                    val users = Repositories.get<UserRepository>()
                    val session = UserSession(
                        newSnowflake(),
                        pending.name,
                        pending.email
                    )

                    users[session.id.value] = User.new(session, pending.password).toDTO()

                    Repositories.get<VerificationRepository>().remove(pending.id)

                    call.frontend("login")
                }
            }

            user()

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
}