package dev.kamiql.dto

abstract class DTOConvertible<T: DTO<*>> {
    abstract fun toDTO(): T
}