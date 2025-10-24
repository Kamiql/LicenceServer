package dev.kamiql.model.verification

import java.time.Instant
import java.util.UUID

data class PendingVerification(
    val id: String = UUID.randomUUID().toString(),
    val email: String,
    val name: String,
    val password: String,
    val createdAt: Long = Instant.now().epochSecond
)