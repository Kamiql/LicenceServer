package dev.kamiql.repository.verification

import com.google.gson.reflect.TypeToken
import dev.kamiql.model.verification.PendingVerification
import dev.kamiql.util.database.types.MongoRepository

class VerificationRepository : MongoRepository<String, PendingVerification>(
    "pending_verifications",
    String::class.java,
    object : TypeToken<PendingVerification>(){}.type
)