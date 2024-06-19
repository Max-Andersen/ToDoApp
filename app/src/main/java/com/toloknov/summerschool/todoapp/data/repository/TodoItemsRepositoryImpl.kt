package com.toloknov.summerschool.todoapp.data.repository

import android.util.Log
import com.toloknov.summerschool.todoapp.data.mapper.toDomain
import com.toloknov.summerschool.todoapp.data.mapper.toEntity
import com.toloknov.summerschool.todoapp.data.model.TodoItemEntity
import com.toloknov.summerschool.todoapp.domain.api.TodoItemsRepository
import com.toloknov.summerschool.todoapp.domain.model.ItemImportance
import com.toloknov.summerschool.todoapp.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.ZoneId

class TodoItemsRepositoryImpl : TodoItemsRepository {

    private val items = mutableListOf(
        TodoItemEntity(
            id = "1",
            text = "Buy groceries",
            importance = ItemImportance.LOW,
            isDone = false,
            creationDate = Instant.now().atZone(ZoneId.systemDefault()),
            deadlineTs = Instant.now().plusSeconds(3600 * 24).atZone(ZoneId.systemDefault())
        ),
        TodoItemEntity(
            id = "2",
            text = "Pay bills",
            importance = ItemImportance.HIGH,
            isDone = true,
            creationDate = Instant.now().minusSeconds(3600 * 24 * 2).atZone(ZoneId.systemDefault()),
            deadlineTs = Instant.now().minusSeconds(3600 * 24).atZone(ZoneId.systemDefault()),
            updateTs = Instant.now().minusSeconds(3600 * 12).atZone(ZoneId.systemDefault())
        ),
        TodoItemEntity(
            id = "3",
            text = "Schedule meeting",
            importance = ItemImportance.COMMON,
            isDone = false,
            creationDate = Instant.now().minusSeconds(3600 * 24 * 3).atZone(ZoneId.systemDefault()),
            deadlineTs = Instant.now().plusSeconds(3600 * 48).atZone(ZoneId.systemDefault())
        ),
        TodoItemEntity(
            id = "4",
            text = "Visit doctor",
            importance = ItemImportance.HIGH,
            isDone = true,
            creationDate = Instant.now().minusSeconds(3600 * 24 * 5).atZone(ZoneId.systemDefault())
        ),
        TodoItemEntity(
            id = "5",
            text = "Read book",
            importance = ItemImportance.LOW,
            isDone = false,
            creationDate = Instant.now().minusSeconds(3600 * 24 * 10)
                .atZone(ZoneId.systemDefault()),
            updateTs = Instant.now().minusSeconds(3600 * 24 * 3).atZone(ZoneId.systemDefault())
        ),
        TodoItemEntity(
            id = "6",
            text = "Exercise",
            importance = ItemImportance.COMMON,
            isDone = true,
            creationDate = Instant.now().minusSeconds(3600 * 24 * 7).atZone(ZoneId.systemDefault()),
            deadlineTs = Instant.now().minusSeconds(3600 * 24 * 6).atZone(ZoneId.systemDefault())
        ),
        TodoItemEntity(
            id = "7",
            text = "Write report",
            importance = ItemImportance.HIGH,
            isDone = false,
            creationDate = Instant.now().minusSeconds(3600 * 24).atZone(ZoneId.systemDefault()),
            deadlineTs = Instant.now().plusSeconds(3600 * 24 * 3).atZone(ZoneId.systemDefault())
        ),
        TodoItemEntity(
            id = "8",
            text = "Plan vacation",
            importance = ItemImportance.LOW,
            isDone = false,
            creationDate = Instant.now().atZone(ZoneId.systemDefault())
        ),
        TodoItemEntity(
            id = "9",
            text = "Clean house",
            importance = ItemImportance.COMMON,
            isDone = true,
            creationDate = Instant.now().minusSeconds(3600 * 24 * 12)
                .atZone(ZoneId.systemDefault()),
            deadlineTs = Instant.now().minusSeconds(3600 * 24 * 2).atZone(ZoneId.systemDefault())
        ),
        TodoItemEntity(
            id = "10",
            text = "Organize files",
            importance = ItemImportance.LOW,
            isDone = false,
            creationDate = Instant.now().minusSeconds(3600 * 24 * 15)
                .atZone(ZoneId.systemDefault()),
            updateTs = Instant.now().minusSeconds(3600 * 24 * 7).atZone(ZoneId.systemDefault())
        ),
        TodoItemEntity(
            id = "11",
            text = "Organize files",
            importance = ItemImportance.LOW,
            isDone = false,
            creationDate = Instant.now().minusSeconds(3600 * 24 * 15)
                .atZone(ZoneId.systemDefault()),
            updateTs = Instant.now().minusSeconds(3600 * 24 * 7).atZone(ZoneId.systemDefault())
        ),
        TodoItemEntity(
            id = "12",
            text = "Organize files",
            importance = ItemImportance.LOW,
            isDone = false,
            creationDate = Instant.now().minusSeconds(3600 * 24 * 15)
                .atZone(ZoneId.systemDefault()),
            updateTs = Instant.now().minusSeconds(3600 * 24 * 7).atZone(ZoneId.systemDefault())
        ),

        TodoItemEntity(
            id = "13",
            text = "Organize files",
            importance = ItemImportance.LOW,
            isDone = false,
            creationDate = Instant.now().minusSeconds(3600 * 24 * 15)
                .atZone(ZoneId.systemDefault()),
            updateTs = Instant.now().minusSeconds(3600 * 24 * 7).atZone(ZoneId.systemDefault())
        ),

        TodoItemEntity(
            id = "14",
            text = "Organize files",
            importance = ItemImportance.LOW,
            isDone = false,
            creationDate = Instant.now().minusSeconds(3600 * 24 * 15)
                .atZone(ZoneId.systemDefault()),
            updateTs = Instant.now().minusSeconds(3600 * 24 * 7).atZone(ZoneId.systemDefault())
        ),


        )

    private val dataFlow: MutableStateFlow<List<TodoItemEntity>> = MutableStateFlow(items)

    override fun getAllItems(): Flow<List<TodoItem>> =
        flow {
            dataFlow.collect {
                Log.d("TodoItemsRepositoryImpl", "collect ${dataFlow.value.size}")

                emit(it.map { it.toDomain() })
            }
        }

    override suspend fun getById(itemId: String): TodoItem? =
        dataFlow.value.find { entity -> entity.id == itemId }?.toDomain()

    override suspend fun addItem(item: TodoItem) {
        val newList = dataFlow.value.toMutableList()
        newList.add(item.toEntity())
        dataFlow.emit(newList)
    }

    override suspend fun updateItem(item: TodoItem) {
        val items = dataFlow.value.toMutableList()
        val index = items.indexOfFirst { it.id == item.id }
        if (index == -1) return
        items[index] = item.toEntity()
        dataFlow.emit(items)
    }

    override suspend fun setDoneStatusForItem(itemId: String, isDone: Boolean) {
        val items = dataFlow.value.toMutableList()
        val index = items.indexOfFirst { it.id == itemId }
        if (index == -1) return
        items[index] = items[index].copy(isDone = isDone)
        dataFlow.emit(items)
    }

    override suspend fun removeItem(itemId: String) {
        val items = dataFlow.value.toMutableList()
        val index = items.indexOfFirst { it.id == itemId }
        if (index == -1) return
        items.removeAt(index)
        dataFlow.emit(items)
    }
}