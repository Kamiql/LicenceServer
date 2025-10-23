package dev.kamiql.api.user

import dev.kamiql.model.user.groups.Group
import dev.kamiql.repository.user.UserRepository
import dev.kamiql.session
import dev.kamiql.util.database.Repositories
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.user() {
    route("/users") {
        val users = Repositories.get<UserRepository>()

        get {
            session(Group.ADMINISTRATOR) {

            }
        }
    }
}

