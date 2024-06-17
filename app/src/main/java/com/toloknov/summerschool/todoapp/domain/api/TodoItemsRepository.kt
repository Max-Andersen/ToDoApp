package com.toloknov.summerschool.todoapp.domain.api

import com.toloknov.summerschool.todoapp.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoItemsRepository {
    fun getAllItems(): Flow<TodoItem>

    suspend fun getById(itemId: String): TodoItem?

    suspend fun addItem(item: TodoItem)

    suspend fun updateItem(item: TodoItem)

    suspend fun removeItem(itemId: String)
}