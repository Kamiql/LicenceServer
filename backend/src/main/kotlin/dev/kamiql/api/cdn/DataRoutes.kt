package dev.kamiql.api.cdn

import dev.kamiql.model.user.groups.Group
import dev.kamiql.session
import dev.kamiql.util.data.DataStorage
import io.ktor.http.*
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

inline fun <reified T : DataStorage> Route.cdnRoute(src: DataStorage) {
    DataStorage.register(src, T::class.java)

    route("/api/cdn/${src.id}") {
        get("/{file}") {
            val fileName = call.parameters["file"] ?:
            return@get call.respond(HttpStatusCode.BadRequest, "Missing file parameter")

            try {
                src.serve(call, fileName)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid file")
            }
        }

        post("/") {
            val token = call.parameters["token"]

            session(Group.ADMINISTRATOR, require = { user ->
                token?.let { src.evaluateToken(user.id, it) } ?: false
            }) {
                val multipart = call.receiveMultipart()
                multipart.forEachPart { part ->
                    if (part is PartData.FileItem) {
                        val filename = part.originalFileName ?: "file"
                        val bytes = part.streamProvider().readBytes()
                        src.save(filename, bytes)
                    }
                    part.dispose()
                }
                call.respond(HttpStatusCode.OK)
            }
        }

        delete("/{file}") {
            session(Group.ADMINISTRATOR) {
                val fileName = call.parameters["file"] ?:
                return@session call.respond(HttpStatusCode.BadRequest, "Missing file parameter")

                val file = src.resolvePath(fileName)
                if (file.exists()) file.delete()
                call.respond(HttpStatusCode.OK)
            }
        }

        get {
            session(Group.ADMINISTRATOR) {
                val dir = File("data/${src.id}")
                if (!dir.exists() || !dir.isDirectory) {
                    call.respond(emptyList<String>())
                    return@session
                }
                val files = dir.walkTopDown()
                    .filter { it.isFile }
                    .map { it.relativeTo(dir).path.replace("\\", "/") }
                    .toList()
                call.respond(files)
            }
        }
    }
}
