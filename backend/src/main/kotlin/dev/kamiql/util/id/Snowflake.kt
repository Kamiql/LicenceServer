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

        fun generate(): Long {
            val timestamp = System.currentTimeMillis() - DISCORD_EPOCH
            val workerId = (Math.random() * 32).toLong() and 0x1F
            val processId = (Math.random() * 32).toLong() and 0x1F
            val increment = (Math.random() * 4096).toLong() and 0xFFF
            return (timestamp shl 22) or (workerId shl 17) or (processId shl 12) or increment
        }
    }
}

inline fun <reified T : Snowflake> snowflake(value: Long): T {
    return T::class.constructors.first().call(value)
}


inline fun <reified T : Snowflake> snowflake(value: String): T {
    return T::class.constructors.first().call(value.toLong())
}

inline fun <reified T : Snowflake> newSnowflake(): T {
    return T::class.constructors.first().call(Snowflake.generate())
}