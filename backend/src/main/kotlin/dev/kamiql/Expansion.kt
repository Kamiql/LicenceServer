@file:Suppress("DEPRECATION")

package dev.kamiql

import dev.kamiql.dto.DTO
import dev.kamiql.model.session.UserSession
import dev.kamiql.model.user.User
import dev.kamiql.model.user.groups.Group
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.RoutingContext
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import org.mindrot.jbcrypt.BCrypt

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

fun hash(password: String): String =
    BCrypt.hashpw(password, BCrypt.gensalt())

fun verify(password: String, hashed: String): Boolean =
    BCrypt.checkpw(password, hashed)

suspend fun RoutingCall.frontend(relative: String) {
    respondRedirect("http://localhost:3000/$relative")
}

suspend inline fun <reified T> RoutingContext.queryParam(
    name: String,
    default: T? = null,
    converter: (String) -> T? = { it as? T }
): T? {
    val value = call.request.queryParameters[name] ?: return default
    return converter(value) ?: default
}

suspend inline fun <reified T> RoutingContext.requireQueryParam(
    name: String,
    converter: (String) -> T? = { it as? T }
): T {
    return queryParam(name, null, converter)
        ?: call.respond(HttpStatusCode.NotFound, "Missing or invalid query parameter: $name")
            .let { throw IllegalStateException("unreachable") }
}