package dev.kamiql.util.data.types

import dev.kamiql.util.data.DataStorage
import java.io.File

class FileDataStorage(
    id: String,
    private vararg val allowedExtensions: String
) : DataStorage(id) {
    override fun resolvePath(path: String): File {
        val file = File("data/$id/$path")
        if (allowedExtensions.isNotEmpty() && !allowedExtensions.any { file.name.endsWith(it) }) {
            throw IllegalArgumentException("File extension not allowed: ${file.extension}")
        }
        return file
    }
}
