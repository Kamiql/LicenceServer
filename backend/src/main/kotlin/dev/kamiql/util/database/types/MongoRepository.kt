package dev.kamiql.util.database.types

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.UpdateOptions
import dev.kamiql.util.database.Repository
import dev.kamiql.util.tasks.TaskScheduler
import dev.kamiql.util.gson.toJson
import dev.kamiql.util.gson.toObject
import org.litote.kmongo.*
import java.lang.reflect.Type
import java.time.Duration

abstract class MongoRepository<K: Any, V: Any>(val name: String, override val kType: Type, override val vType: Type) : Repository<K, V>(kType, vType) {
    lateinit var db: MongoDatabase
    lateinit var collection: MongoCollection<DBObject>
    lateinit var taskScheduler: TaskScheduler

    open fun onDataLoad() {
        println("Loaded data for: $name")
        data.forEach { (key, value) ->
            println("$key: $value")
        }
    }

    override fun load() {
        collection = db.getCollection<DBObject>(name)

        taskScheduler.runSync {
            sync()
            onDataLoad()
        }

        taskScheduler.runAsyncTimer(Duration.ofMinutes(5), Duration.ofMinutes(5)) {
            sync()
        }
    }

    fun sync() {
        collection.find().mapNotNull {
            it.toObject(this)
        }.toMap().forEach(data::set)
    }

    override fun save(key: K, value: V) {
        val obj = DBObject.from(key, value)
        taskScheduler.runAsync {
            collection.updateOne(
                DBObject::id eq obj.id,
                setValue(DBObject::json, obj.json),
                UpdateOptions().upsert(true)
            )
        }
    }

    override fun delete(key: K) {
        taskScheduler.runAsync {
            val id = key.toJson()
            collection.deleteOne(DBObject::id eq id)
        }
    }

    override fun debug(message: String) {

    }

    override fun close() {
        taskScheduler.runSync {
            data.forEach { (key, value) ->
                val obj = DBObject.from(key, value)
                collection.updateOne(
                    DBObject::id eq obj.id,
                    setValue(DBObject::json, obj.json),
                    UpdateOptions().upsert(true)
                )
            }
        }
    }

    class DBObject(val id: String, val json: String) {
        fun <K: Any, T: Any> toObject(repo: Repository<K, T>): Pair<K, T>? {
            return try {
                val key = id.toObject<K>(repo.kType) as K
                val value = json.toObject<T>(repo.vType) as T
                key to value
            } catch (e: Exception) {
                repo.onFail()
                null
            }
        }

        companion object {
            fun <K : Any, T: Any> from(key: K, value: T): DBObject {
                return DBObject(
                    key.toJson(),
                    value.toJson()
                )
            }
        }
    }
}