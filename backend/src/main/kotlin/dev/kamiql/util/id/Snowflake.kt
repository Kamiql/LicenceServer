package dev.kamiql.util.id

import kotlinx.serialization.Serializable

@Serializable
abstract class Snowflake(val value: Long) {
    val timestamp: Long get() = (value shr 22) + DISCORD_EPOCH
    val workerId: Long get() = (value shr 17) and 0x1F
    val processId: Long get() = (value shr 12) and 0x1F
    val increment: Long get() = value and 0xFFF

    companion object {
        const val DISCORD_EPOCH = 1420070400000L
    }
}

inline fun <reified T : Snowflake> snowflake(value: Long): T {
    return T::class.constructors.first().call(value)
}


inline fun <reified T : Snowflake> snowflake(value: String): T {
    return T::class.constructors.first().call(value.toLong())
}