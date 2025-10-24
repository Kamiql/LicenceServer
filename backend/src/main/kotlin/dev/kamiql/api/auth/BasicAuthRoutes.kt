package dev.kamiql.api.auth

import dev.kamiql.model.session.UserSession
import dev.kamiql.model.verification.PendingVerification
import dev.kamiql.repository.user.UserRepository
import dev.kamiql.repository.verification.VerificationRepository
import dev.kamiql.util.database.Repositories
import dev.kamiql.util.mail.MailService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set

data class LoginForm(
    val credential: String,
    val password: String,
)

data class RegisterForm(
    val email: String,
    val name: String,
    val password: String,
)

fun Route.basicAuth() {
    val users = Repositories.get<UserRepository>()

    val mailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    val usernameRegex = Regex("^(?=.{2,32}$)(?!(?:everyone|here)$)[a-z0-9_-]+$")

    route("/basic") {
        post("/login") {
            val form = call.receive<LoginForm>()
            val credential = form.credential.trim().lowercase()

            val user = when {
                mailRegex.matches(credential) -> users.findByEmail(credential)
                usernameRegex.matches(credential) -> users.findByName(credential)
                else -> {
                    call.respond(HttpStatusCode.BadRequest, "Invalid credential format")
                    return@post
                }
            }?.toModel() ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid credentials")

            if (!user.validatePassword(form.password)) return@post call.respond(HttpStatusCode.BadRequest, "Invalid password")

            call.sessions.set<UserSession>(user.toSession())
            call.respond(HttpStatusCode.OK)
        }

        post("/register") {
            val form = call.receive<RegisterForm>()

            if (!usernameRegex.matches(form.name)) return@post call.respond(HttpStatusCode.BadRequest, "Invalid username")
            if (!mailRegex.matches(form.email)) return@post call.respond(HttpStatusCode.BadRequest, "Invalid email")

            val pendingRepo = Repositories.get<VerificationRepository>()

            if (users.values().any {
                    it.username.equals(form.name, true) || it.email == form.email
                }) return@post call.respond(HttpStatusCode.BadRequest, "username or email already taken")

            val pending = PendingVerification(
                email = form.email,
                name = form.name,
                password = form.password
            )

            pendingRepo[pending.id] = pending

            MailService.send(
                "verify.md",
                to = form.email,
                variables = mapOf(
                    "username" to form.name,
                    "link" to "http://localhost:3000/verify?id=${pending.id}",
                    "subject" to "Bitte bestätige deine E-Mail-Adresse"
                )
            )

            call.respond(HttpStatusCode.OK, "Verification mail sent")
        }
    }
}