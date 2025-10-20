package dev.kamiql.database.types

import dev.kamiql.database.Repository
import java.lang.reflect.Type

abstract class MongoRepository<K: Any, V: Any>(override val kType: Type, override val vType: Type) : Repository<K, V>(kType, vType) {

}