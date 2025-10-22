package dev.kamiql.dto

abstract class DTO<T: Any> {
    abstract fun toModel(): T
}