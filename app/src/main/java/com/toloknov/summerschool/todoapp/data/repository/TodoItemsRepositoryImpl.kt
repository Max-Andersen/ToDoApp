package com.toloknov.summerschool.todoapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import com.toloknov.summerschool.todoapp.NetworkPreferences
import com.toloknov.summerschool.todoapp.data.local.db.model.toDomain
import com.toloknov.summerschool.todoapp.data.local.db.model.toEntity
import com.toloknov.summerschool.todoapp.data.local.db.model.TodoItemEntity
import com.toloknov.summerschool.todoapp.data.remote.TodoApi
import com.toloknov.summerschool.todoapp.data.remote.model.ItemTransmitModel
import com.toloknov.summerschool.todoapp.data.remote.model.toDomain
import com.toloknov.summerschool.todoapp.data.remote.model.toRest
import com.toloknov.summerschool.todoapp.domain.api.TodoItemsRepository
import com.toloknov.summerschool.todoapp.domain.model.TodoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Вся жесть и манипулирование [localData] в этом репозитории - зашлушка работы с БД
 * Как подключу Room код станет очевидно лакониченее, но в этом ТЗ БД не обязательна
 *
 * то что тут в зависимостях и DataStore<NetworkPreferences> ещё не ломает Single reponsibility
 * нельзя, чтобы репозиторий использовал репозиторий, а вот если оба имеют доступ к одному ресурсу, то нормально
 */
class TodoItemsRepositoryImpl(
    private val api: TodoApi,
    private val networkDataStore: DataStore<NetworkPreferences>
) : TodoItemsRepository {

    private val items = mutableListOf<TodoItemEntity>()

    private val localData: MutableStateFlow<List<TodoItemEntity>> = MutableStateFlow(items)

    override suspend fun getRemoteItems(): List<TodoItem> = withContext(Dispatchers.IO) {
        val remoteState = api.getAllItems()
        val remoteItems = remoteState.body()?.list?.map { it.toDomain() } ?: listOf()

        localData.emit(remoteItems.map { it.toEntity() })

        // save to local
        return@withContext remoteItems
    }

    override fun getLocalItems(): Flow<List<TodoItem>> {
        return flow {
            localData.collect {
                emit(it.map { it.toDomain() })
            }
        }
    }

    override suspend fun getById(itemId: String): Result<TodoItem?> = withContext(Dispatchers.IO) {
        return@withContext try {
            val remoteItemResponse = api.getItemById(itemId)
            if (remoteItemResponse.isSuccessful) {
                Result.success(remoteItemResponse.body()?.element?.toDomain())
            } else {
                Result.failure(Exception("Ошибка получения напоминания"))
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
                updateRevisionInDataStore(revision + 1)
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
                updateRevisionInDataStore(revision + 1)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Неудачный запрос"))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    override suspend fun setDoneStatusForItem(itemId: String, isDone: Boolean): Result<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val items = localData.value.toMutableList()
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
                    updateRevisionInDataStore(revision + 1)
                    items[index] = items[index].copy(isDone = isDone)
                    localData.emit(items)
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
                val items = localData.value.toMutableList()
                val index = items.indexOfFirst { it.id == itemId }
                if (index == -1) return@withContext Result.failure(Exception("Элемент не найден"))
                items.removeAt(index)
                localData.emit(items)
                updateRevisionInDataStore(revision + 1)
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
                updateRevisionInDataStore(it)
            }
            val remoteItems = remoteState.body()?.list?.map { it.toDomain() } ?: listOf()

            localData.emit(remoteItems.map { it.toEntity() })
        }
    }

    override suspend fun syncItemsWithResult(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val remoteState = api.getAllItems()

            return@withContext if (remoteState.isSuccessful) {
                remoteState.body()?.revision?.let {
                    updateRevisionInDataStore(it)
                }
                val remoteItems = remoteState.body()?.list?.map { it.toDomain() } ?: listOf()

                localData.emit(remoteItems.map { it.toEntity() })
                Result.success(Unit)
            } else {
                Result.failure(Exception("Ошибка получения данных"))
            }
        }
    }

    private suspend fun updateRevisionInDataStore(revision: Int){
        networkDataStore.updateData { prefs ->
            prefs.toBuilder().setRevision(revision).build()
        }
    }

    override suspend fun isRemoteRevisionLarger(): Boolean {
        // задел на будущее
        TODO("Not yet implemented")
    }

    override suspend fun overrideLocalChanges() {
        // задел на будущее
        TODO("Not yet implemented")
    }
}