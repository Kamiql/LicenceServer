package dev.kamiql.util.gson

import com.google.gson.*
import io.ktor.http.ContentType
import io.ktor.serialization.Configuration
import io.ktor.serialization.gson.GsonConverter
import java.lang.reflect.Type

object GsonUtil {
    private var gson: Gson = GsonBuilder()
        .serializeNulls()
        .setStrictness(Strictness.LENIENT)
        .create()
    var logger: ((Throwable) -> Unit)? = null
        private set

    fun builder(block: (GsonBuilder) -> GsonBuilder) {
        gson = block(gson.newBuilder()).create()
    }

    fun logger(block: (Throwable) -> Unit) {
        logger = block
    }

    fun gson(): Gson = gson
}

fun <T> String.toObject(type: Type): T? = try {
    GsonUtil.gson().fromJson(this, type)
} catch (ex: Exception) {
    GsonUtil.logger?.invoke(ex)
    null
}

inline fun <reified T> String.toObject(): T? = try {
    GsonUtil.gson().fromJson(this, T::class.java)
} catch (ex: Exception) {
    GsonUtil.logger?.invoke(ex)
    null
}

inline fun <reified T> JsonElement.toObject(): T? = try {
    GsonUtil.gson().fromJson(this, T::class.java)
} catch (ex: Exception) {
    GsonUtil.logger?.invoke(ex)
    null
}

fun Any.toJson(): String = try {
    GsonUtil.gson().toJson(this)
} catch (ex: Exception) {
    GsonUtil.logger?.invoke(ex)
    "{}"
}

fun Any.toPrettyJson(): String = try {
    GsonUtil.gson()
        .newBuilder()
        .setPrettyPrinting()
        .create()
        .toJson(this)
} catch (ex: Exception) {
    GsonUtil.logger?.invoke(ex)
    "{}"
}

fun Any.toJsonElement(): JsonElement? = try {
    GsonUtil.gson().toJsonTree(this)
} catch (ex: Exception) {
    GsonUtil.logger?.invoke(ex)
    null
}

fun Configuration.gson(
    contentType: ContentType = ContentType.Application.Json,
    builder: GsonBuilder
) {
    val converter = GsonConverter(builder.create())
    register(contentType, converter)
}

fun Configuration.gson(
    contentType: ContentType = ContentType.Application.Json,
    builder: Gson
) {
    val converter = GsonConverter(builder)
    register(contentType, converter)
}