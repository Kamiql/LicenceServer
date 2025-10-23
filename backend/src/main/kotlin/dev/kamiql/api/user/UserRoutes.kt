package dev.kamiql.api.user

import dev.kamiql.dto.DTOList
import dev.kamiql.model.user.User
import dev.kamiql.model.user.UserDTO
import dev.kamiql.model.user.groups.Group
import dev.kamiql.param
import dev.kamiql.repository.user.UserRepository
import dev.kamiql.respondDTO
import dev.kamiql.session
import dev.kamiql.util.database.Repositories
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.user() {
    route("/users") {
        val users = Repositories.get<UserRepository>()

        get {
            session(Group.ADMINISTRATOR) {
                call.respondDTO(DTOList(users.values()))
            }
        }

        get("/{user}") {
            val user = param<UserDTO>("user") { id ->
                users[id.toLong()]
            } ?: return@get call.respond(HttpStatusCode.BadRequest)

            call.respondDTO(user)
        }

        post {
            session(Group.ADMINISTRATOR) {
                val user = call.receive<UserDTO>()
                users.mutate(user.id) {
                    user
                }

                call.respond(HttpStatusCode.OK)
            }
        }

        delete("/{user}") {
            session(Group.ADMINISTRATOR) {
                val user = param<UserDTO>("user") { id ->
                    users[id.toLong()]
                } ?: return@session call.respond(HttpStatusCode.BadRequest)
                users.remove(user.id)
            }
        }
    }
}

