package dev.kamiql.util.data

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

abstract class DataStorage(val id: String) {
    abstract fun resolvePath(path: String): File

    suspend fun serve(call: ApplicationCall, path: String) {
        val file = resolvePath(path)
        if (!file.exists() || !file.isFile) {
            call.respond(HttpStatusCode.NotFound, "File not found")
            return
        }
        call.respondFile(file)
    }

    fun save(path: String, data: ByteArray): File {
        val file = resolvePath(path)
        file.parentFile?.mkdirs()
        file.writeBytes(data)
        return file
    }

    fun delete(path: String): Boolean {
        val file = resolvePath(path)
        return file.exists() && file.delete()
    }

    fun exists(path: String): Boolean {
        return resolvePath(path).exists()
    }

    fun getUrl(path: String): String? {
        val file = resolvePath(path)
        return if (file.exists() && file.isFile) "/cdn/$id/$path" else null
    }

    fun fromUrl(url: String): String? {
        val prefix = "/cdn/$id/"
        return if (url.startsWith(prefix)) url.removePrefix(prefix) else null
    }

    companion object {
        val storages = mutableMapOf<Class<out DataStorage>, MutableList<DataStorage>>()

        fun register(src: DataStorage, clazz: Class<out DataStorage>) {
            storages.computeIfAbsent(clazz) { mutableListOf() }.add(src)
        }

        operator fun get(id: String): DataStorage =
            storages.values.flatten().first { it.id == id }

        inline fun <reified T : DataStorage> type(): List<DataStorage> =
            storages[T::class.java] ?: emptyList()

        inline fun <reified T : DataStorage> find(id: String): DataStorage =
            type<T>().first { it.id == id }
    }
}

inline fun <reified T: DataStorage> Route.cdnRoute(src: DataStorage) {
    DataStorage.register(src, T::class.java)

    get("/cdn/${src.id}") {
        val fileName = call.request.queryParameters["file"]
        if (fileName == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing 'file' query parameter")
            return@get
        }
        try {
            src.serve(call, fileName)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid file")
        }
    }

    get("/cdn/${src.id}/list") {
        val dir = File("data/${src.id}")
        if (!dir.exists() || !dir.isDirectory) {
            call.respond(emptyList<String>())
            return@get
        }
        val files = dir.walkTopDown()
            .filter { it.isFile }
            .map { it.relativeTo(dir).path.replace("\\", "/") }
            .toList()
        call.respond(files)
    }
}
