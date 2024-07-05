package com.toloknov.summerschool.todoapp.domain.api

import com.toloknov.summerschool.todoapp.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoItemsRepository {
    suspend fun getRemoteItems(): List<TodoItem>

    fun getLocalItems(): Flow<List<TodoItem>>

    suspend fun getById(itemId: String): Result<TodoItem?>

    suspend fun addItem(item: TodoItem): Result<Unit>

    suspend fun updateItem(item: TodoItem): Result<Unit>

    suspend fun setDoneStatusForItem(itemId: String, isDone: Boolean): Result<Unit>

    suspend fun removeItem(itemId: String): Result<Unit>

    fun syncItems()

    suspend fun isRemoteRevisionLarger(): Boolean

    suspend fun overrideLocalChanges()
}