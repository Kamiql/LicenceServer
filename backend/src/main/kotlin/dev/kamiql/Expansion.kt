package dev.kamiql

import dev.kamiql.dto.DTO
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall

object AuthType {
    const val SESSION = "auth-session"
    const val OAUTH_DISCORD = "auth-oauth-discord"
}

suspend fun RoutingCall.respondDTO(dto: DTO<*>) {
    respond(dto)
}

fun Any?.isNull(): Boolean = this == null

fun Any?.isNotNull(): Boolean = this != null

infix fun Boolean.isTrue(action: () -> Unit) {
    if (this) action()
}

infix fun Boolean.isFalse(action: () -> Unit) {
    if (!this) action()
}