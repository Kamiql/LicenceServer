package dev.kamiql.util.id

abstract class Snowflake(val value: Long) {
    val timestamp: Long get() = (value shr 22) + DISCORD_EPOCH
    val workerId: Long get() = (value shr 17) and 0x1F
    val processId: Long get() = (value shr 12) and 0x1F
    val increment: Long get() = value and 0xFFF

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Snowflake) return false
        return value == other.value
    }

    override fun hashCode(): Int = value.hashCode()

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