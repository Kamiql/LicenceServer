package dev.kamiql.util.gson.serializers

import dev.kamiql.util.gson.GsonUtil
import io.ktor.server.sessions.SessionSerializer

class GsonSessionSerializer<T>(private val clazz: Class<T>) : SessionSerializer<T> {
    override fun deserialize(text: String): T = GsonUtil.gson().fromJson(text, clazz)
    override fun serialize(session: T): String = GsonUtil.gson().toJson(session)
}