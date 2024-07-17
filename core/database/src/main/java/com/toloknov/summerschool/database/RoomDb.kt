package com.toloknov.summerschool.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.toloknov.summerschool.database.dao.TodoDao
import com.toloknov.summerschool.database.model.TodoItemEntity
import com.toloknov.summerschool.database.utils.DbConverter

const val DB_VERSION = 2

@Database(
    version = DB_VERSION,
    exportSchema = true,
    entities = [
        TodoItemEntity::class
    ],
    autoMigrations = [

    ],
)
@TypeConverters(
    DbConverter::class,
)
abstract class RoomDb : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        // Название файла с БД
        private const val DB_FILE = "todo_db"

        // Конструктор подключения к БД
        fun databaseBuilder(appContext: Context): RoomDb {
            val preDb = Room.databaseBuilder(appContext, RoomDb::class.java, DB_FILE)
//                .fallbackToDestructiveMigration()           // ТОЛЬКО ДЛЯ ОТЛАДКИ! РАЗРУШАЕТ БД!

            // Добавить миграции
//            DB_MIGRATIONS.forEach { migration ->
//                preDb.addMigrations(migration)
//            }

            return preDb.build()
        }
    }
}