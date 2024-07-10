package com.toloknov.summerschool.todoapp.data.repository

import androidx.datastore.core.DataStore
import com.toloknov.summerschool.todoapp.NetworkPreferences
import com.toloknov.summerschool.todoapp.data.local.db.dao.TodoDao
import com.toloknov.summerschool.todoapp.data.local.db.model.TodoItemEntity
import com.toloknov.summerschool.todoapp.data.local.db.model.toDomain
import com.toloknov.summerschool.todoapp.data.local.db.model.toEntity
import com.toloknov.summerschool.todoapp.data.local.db.utils.TransactionProvider
import com.toloknov.summerschool.todoapp.data.remote.TodoApi
import com.toloknov.summerschool.todoapp.data.remote.model.ItemTransmitModel
import com.toloknov.summerschool.todoapp.data.remote.model.ItemsListTransmitModel
import com.toloknov.summerschool.todoapp.data.remote.model.toDomain
import com.toloknov.summerschool.todoapp.data.remote.model.toRest
import com.toloknov.summerschool.todoapp.data.util.ItemListMerger
import com.toloknov.summerschool.todoapp.domain.api.TodoItemsRepository
import com.toloknov.summerschool.todoapp.domain.model.TodoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject


class TodoItemsRepositoryImpl
@Inject constructor(
    private val dao: TodoDao,
    private val api: TodoApi,
    private val networkDataStore: DataStore<NetworkPreferences>,
    private val transactionProvider: TransactionProvider,
    private val itemListMerger: ItemListMerger
) : TodoItemsRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val atomicDispatcher = Dispatchers.IO.limitedParallelism(1)

    private val localData: MutableStateFlow<List<TodoItemEntity>> = MutableStateFlow(emptyList())

    override fun getItems(): Flow<List<TodoItem>> {
        CoroutineScope(atomicDispatcher).launch {
            syncItems()
        }
        return dao.selectAll().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getById(itemId: String): TodoItem? = dao.getById(itemId)?.toDomain()

    override suspend fun addItem(item: TodoItem) {
        withContext(atomicDispatcher) {
            dao.insert(item.toEntity())

            val revision = getCurrentRevision()

            val model =
                ItemTransmitModel(
                    ok = true,
                    element = item.toRest()
                )

            val response = api.addItem(revision, model)

            if (response.isSuccessful) {
                updateRevisionInDataStore(revision + 1)
            }
        }
    }

    override suspend fun updateItem(item: TodoItem) {
        withContext(atomicDispatcher) {
            dao.update(item.toEntity())

            val revision = getCurrentRevision()
            val model =
                ItemTransmitModel(
                    ok = true,
                    element = item.toRest()
                )

            val response = api.updateItemById(id = item.id, revision = revision, body = model)

            if (response.isSuccessful) {
                updateRevisionInDataStore(revision + 1)
            }
        }
    }

    override suspend fun setDoneStatusForItem(itemId: String, isDone: Boolean) =
        withContext(atomicDispatcher) {
            val item = dao.getById(itemId)
            if (item != null) {
                dao.update(item.copy(isDone = isDone))

                val revision = getCurrentRevision()
                val model =
                    ItemTransmitModel(
                        ok = true,
                        element = item.toRest()
                    )

                val response = api.updateItemById(id = item.id, revision = revision, body = model)

                if (response.isSuccessful) {
                    updateRevisionInDataStore(revision + 1)
                }
            }
        }

    override suspend fun removeItem(itemId: String) {
        withContext(atomicDispatcher) {

            dao.deleteById(itemId)

            val revision = getCurrentRevision()

            val response = api.deleteItemById(id = itemId, revision = revision)

            if (response.isSuccessful) {
                updateRevisionInDataStore(revision + 1)
            }
        }
    }

    override suspend fun syncItems() {
        CoroutineScope(atomicDispatcher).launch {
            val remoteState = api.getAllItems()
            remoteState.body()?.revision?.let {
                updateRevisionInDataStore(it)
            }
            val remoteItems = remoteState.body()?.list?.map { it.toDomain() } ?: listOf()
            val localItems = dao.selectAll().first().map { item -> item.toDomain() }

            val storeValue = networkDataStore.data.first()
            val lastSyncTime = ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(storeValue.lastUpdateTime),
                ZoneId.systemDefault()
            )
            val resultList = itemListMerger.merge(remoteItems, localItems, lastSyncTime)

            val body = ItemsListTransmitModel(
                ok = true,
                list = resultList.map { it.toRest() }
            )

            dao.deleteAll()
            dao.insertAll(resultList.map { it.toEntity() })

            val response = api.updateItems(storeValue.revision, body)

            response.body()?.revision?.let {
                updateRevisionInDataStore(it)
                networkDataStore.updateData { prevState ->
                    prevState.toBuilder().setLastUpdateTime(ZonedDateTime.now().toEpochSecond())
                        .build()
                }
            }
        }
    }

    override suspend fun syncItemsWithResult(): Result<Unit> {
        return withContext(atomicDispatcher) {
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

    private suspend fun updateRevisionInDataStore(revision: Int) {
        networkDataStore.updateData { prefs ->
            prefs.toBuilder().setRevision(revision).build()
        }
    }

    private suspend fun getCurrentRevision(): Int = networkDataStore.data.first().revision
}