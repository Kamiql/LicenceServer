package dev.kamiql.util.id.types

import dev.kamiql.util.id.Snowflake
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class UserSnowflake(value: Long) : Snowflake(value) {
    object Serializer : KSerializer<UserSnowflake> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UserSnowflake", PrimitiveKind.LONG)

        override fun serialize(
            encoder: Encoder,
            value: UserSnowflake
        ) {
            encoder.encodeLong(value.value)
        }

        override fun deserialize(decoder: Decoder): UserSnowflake {
            return UserSnowflake(decoder.decodeLong())
        }
    }
}