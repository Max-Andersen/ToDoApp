package com.toloknov.summerschool.domain.api

import com.toloknov.summerschool.domain.model.ResponseStatus
import com.toloknov.summerschool.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface TodoItemsRepository {

    fun getActionStatusFlow(): StateFlow<ResponseStatus>

    fun getItems(): Flow<List<TodoItem>>

    suspend fun getById(itemId: String): TodoItem?

    suspend fun addItem(item: TodoItem)

    suspend fun updateItem(item: TodoItem)

    suspend fun setDoneStatusForItem(itemId: String, isDone: Boolean)

    suspend fun removeItem(itemId: String)

    suspend fun syncItems()

    suspend fun syncItemsWithResult(): Result<Unit>
}