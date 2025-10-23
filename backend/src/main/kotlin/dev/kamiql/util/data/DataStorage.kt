package dev.kamiql.util.data

import dev.kamiql.util.id.Snowflake
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import kotlin.jvm.Throws

class StorageException(override val message: String?, val status: HttpStatusCode = HttpStatusCode.BadRequest) : Exception()

abstract class DataStorage(val id: String) {
    private val tokens = mutableMapOf<Snowflake, String>()

    fun generateToken(snowflake: Snowflake): String {
        return buildString {
            val chars = "0123456789abcdef"
            repeat(32) { append(chars.random()) }
        }
    }

    fun evaluateToken(snowflake: Snowflake, token: String): Boolean {
        return tokens.remove(snowflake, token)
    }

    abstract fun resolvePath(path: String): File

    suspend fun serve(call: ApplicationCall, path: String) {
        val file = resolvePath(path)
        if (!file.exists() || !file.isFile) {
            call.respond(HttpStatusCode.NotFound, "File not found")
            return
        }
        call.respondFile(file)
    }

    @Throws(StorageException::class)
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
        return if (file.exists() && file.isFile) "/cdn/$id?file=$path" else null
    }

    fun fromUrl(url: String): String? {
        val prefix = "/cdn/$id"
        if (!url.startsWith(prefix)) return null
        val queryIndex = url.indexOf("?file=")
        if (queryIndex == -1) return null
        return url.substring(queryIndex + 6)
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
