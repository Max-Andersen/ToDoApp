package com.toloknov.summerschool.todoapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.toloknov.summerschool.todoapp.data.local.db.dao.TodoDao
import com.toloknov.summerschool.todoapp.data.local.db.model.TodoItemEntity
import com.toloknov.summerschool.todoapp.data.local.db.utils.DbConverter

const val DB_VERSION = 1

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