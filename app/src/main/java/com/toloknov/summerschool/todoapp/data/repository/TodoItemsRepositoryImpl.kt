package com.toloknov.summerschool.todoapp.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import com.google.gson.Gson
import com.toloknov.summerschool.todoapp.NetworkPreferences
import com.toloknov.summerschool.todoapp.data.local.db.model.toDomain
import com.toloknov.summerschool.todoapp.data.local.db.model.toEntity
import com.toloknov.summerschool.todoapp.data.local.db.model.TodoItemEntity
import com.toloknov.summerschool.todoapp.data.remote.TodoApi
import com.toloknov.summerschool.todoapp.data.remote.model.ItemTransmitModel
import com.toloknov.summerschool.todoapp.data.remote.model.toDomain
import com.toloknov.summerschool.todoapp.data.remote.model.toRest
import com.toloknov.summerschool.todoapp.domain.api.TodoItemsRepository
import com.toloknov.summerschool.todoapp.domain.model.ItemImportance
import com.toloknov.summerschool.todoapp.domain.model.TodoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.ZoneId

class TodoItemsRepositoryImpl(
    private val api: TodoApi,
    private val networkDataStore: DataStore<NetworkPreferences>
) : TodoItemsRepository {

    private val items = mutableListOf<TodoItemEntity>()

    private val dataFlow: MutableStateFlow<List<TodoItemEntity>> = MutableStateFlow(items)

    override suspend fun getRemoteItems(): List<TodoItem> = withContext(Dispatchers.IO) {
        val remoteState = api.getAllItems()
        val remoteItems = remoteState.body()?.list?.map { it.toDomain() } ?: listOf()

        dataFlow.emit(remoteItems.map { it.toEntity() })

        // save to local
        return@withContext remoteItems
    }

    override fun getLocalItems(): Flow<List<TodoItem>> {
        return flow {
            dataFlow.collect {
                emit(it.map { it.toDomain() })
            }
        }
    }

    override suspend fun getById(itemId: String): Result<TodoItem?> = withContext(Dispatchers.IO) {
        return@withContext try {
            val remoteItem = api.getItemById(itemId)
            remoteItem.body().let {
                return@withContext Result.success(it?.element?.toDomain())
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    override suspend fun addItem(item: TodoItem): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val revision = networkDataStore.data.first().revision
            val model =
                ItemTransmitModel(
                    ok = true,
                    element = item.toRest()
                )
            val response = api.addItem(revision, model)

            if (response.isSuccessful) {
                networkDataStore.updateData { prefs ->
                    prefs.toBuilder().setRevision(revision + 1).build()
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("неудачный запрос"))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    override suspend fun updateItem(item: TodoItem): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val revision = networkDataStore.data.first().revision
            val model =
                ItemTransmitModel(
                    ok = true,
                    element = item.toRest()
                )

            val response = api.updateItemById(id = item.id, revision = revision, body = model)

            if (response.isSuccessful) {
                networkDataStore.updateData { prefs ->
                    prefs.toBuilder().setRevision(revision + 1).build()
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Неудачный запрос"))
            }

//            val items = dataFlow.value.toMutableList()
//            val index = items.indexOfFirst { it.id == item.id }
//            if (index == -1) return@withContext Result.failure(Exception())
//            items[index] = item.toEntity()
//            dataFlow.emit(items)
//            Result.success(Unit)


        } catch (e: IOException) {
            Result.failure(e)
        }

    }

    override suspend fun setDoneStatusForItem(itemId: String, isDone: Boolean): Result<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val items = dataFlow.value.toMutableList()
                val index = items.indexOfFirst { it.id == itemId }
                if (index == -1) return@withContext Result.failure(Exception("Элемент не найден"))
                val item = items[index]

                val revision = networkDataStore.data.first().revision
                val model =
                    ItemTransmitModel(
                        ok = true,
                        element = item.copy(isDone = isDone).toRest()
                    )

                val response = api.updateItemById(id = item.id, revision = revision, body = model)

                if (response.isSuccessful) {
                    networkDataStore.updateData { prefs ->
                        prefs.toBuilder().setRevision(revision + 1).build()
                    }
                    items[index] = items[index].copy(isDone = isDone)
                    dataFlow.emit(items)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Неудачный запрос"))
                }

            } catch (e: IOException) {
                Result.failure(e)
            }
        }

    override suspend fun removeItem(itemId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val revision = networkDataStore.data.first().revision
            val response = api.deleteItemById(id = itemId, revision = revision)
            if (response.isSuccessful) {
                val items = dataFlow.value.toMutableList()
                val index = items.indexOfFirst { it.id == itemId }
                if (index == -1) return@withContext Result.failure(Exception("Элемент не найден"))
                items.removeAt(index)
                dataFlow.emit(items)
                networkDataStore.updateData { prefs ->
                    prefs.toBuilder().setRevision(revision + 1).build()
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Неудачный запрос"))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    override fun syncItems() {
        CoroutineScope(Dispatchers.IO).launch {
            val remoteState = api.getAllItems()
            remoteState.body()?.revision?.let {
                networkDataStore.updateData { prefs -> prefs.toBuilder().setRevision(it).build() }
            }
            val remoteItems = remoteState.body()?.list?.map { it.toDomain() } ?: listOf()

            dataFlow.emit(remoteItems.map { it.toEntity() })
        }
    }

    override suspend fun isRemoteRevisionLarger(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun overrideLocalChanges() {
        TODO("Not yet implemented")
    }
}