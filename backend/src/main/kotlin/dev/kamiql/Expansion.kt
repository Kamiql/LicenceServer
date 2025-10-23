@file:Suppress("DEPRECATION")

package dev.kamiql

import dev.kamiql.dto.DTO
import dev.kamiql.model.session.Session
import dev.kamiql.model.session.UserSession
import dev.kamiql.model.user.User
import dev.kamiql.model.user.groups.Group
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.auth.Principal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.intercept
import io.ktor.server.routing.method
import io.ktor.server.routing.route
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import kotlin.reflect.KProperty

object AuthType {
    const val OAUTH_DISCORD = "auth-oauth-discord"
}

suspend fun RoutingCall.respondDTO(dto: DTO<*>) {
    respond(dto)
}

suspend fun RoutingContext.session(
    vararg groups: Group,
    require: ((User) -> Boolean) = { false },
    handler: suspend RoutingContext.(User) -> Unit
) {
    val user = call.sessions.get<UserSession>()?.toUser() ?: return call.respond(HttpStatusCode.Unauthorized)
    if (groups.isEmpty()) return handler(user)
    if (groups.none { user.groups.any { u -> u.hasOrInherits(it) } } && !require(user))
        return call.respond(HttpStatusCode.Forbidden)
    handler(user)
}

fun RoutingContext.param(path: String): String? {
    return call.parameters[path]
}

inline fun <reified T> RoutingContext.param(path: String, convert: (String) -> T?): T? {
    return convert(call.parameters[path] ?: return null)
}