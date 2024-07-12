package com.toloknov.summerschool.todoapp.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.toloknov.summerschool.todoapp.data.local.db.model.TodoItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_item")
    fun selectAll(): Flow<List<TodoItemEntity>>

    @Query("SELECT * FROM todo_item WHERE id=:itemId")
    fun getById(itemId: String): TodoItemEntity?

    @Insert
    fun insert(entity: TodoItemEntity)

    @Upsert
    fun upsertAll(entityList: List<TodoItemEntity>)

    @Update
    fun update(entity: TodoItemEntity)

    @Query("DELETE FROM todo_item WHERE id=:itemId")
    fun deleteById(itemId: String)

    @Query("DELETE FROM todo_item")
    fun deleteAll()

}