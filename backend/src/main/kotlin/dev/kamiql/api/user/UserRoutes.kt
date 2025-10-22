package dev.kamiql.api.user

import dev.kamiql.AuthType
import dev.kamiql.model.user.UserSession
import dev.kamiql.respondDTO
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.user() {
    runCatching {
        authenticate(AuthType.SESSION) {
            route("/users") {
                get("/@me") {
                    val user = call.principal<UserSession>()!!.toUser() ?: run {
                        return@get call.respond(HttpStatusCode.Unauthorized)
                    }

                    call.respondDTO(user.toDTO())
                }
            }
        }
    }.onFailure {it.printStackTrace()}

}