package dev.kamiql.dto

abstract class DTO<T: Any> {
    abstract fun toModel(): T
}

class DTOList<T : Any>(
    val items: List<DTO<T>>
) : DTO<List<T>>() {
    override fun toModel(): List<T> = items.map { it.toModel() }
}