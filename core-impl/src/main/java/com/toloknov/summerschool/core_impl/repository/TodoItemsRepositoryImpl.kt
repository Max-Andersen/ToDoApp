package com.toloknov.summerschool.core_impl.repository

import android.util.Log
import androidx.datastore.core.DataStore
import com.toloknov.summerschool.core_impl.ItemListMerger
import com.toloknov.summerschool.core_impl.remote.toDomain
import com.toloknov.summerschool.core_impl.remote.toRest
import com.toloknov.summerschool.core_impl.remote.utils.RestException
import com.toloknov.summerschool.core_impl.remote.utils.RestException.BadCredentials
import com.toloknov.summerschool.core_impl.remote.utils.RestException.InternalServerError
import com.toloknov.summerschool.core_impl.remote.utils.RestException.NotAuthorize
import com.toloknov.summerschool.core_impl.remote.utils.RestException.NotFound
import com.toloknov.summerschool.core_impl.remote.utils.RestException.UnexpectedRest
import com.toloknov.summerschool.database.dao.TodoDao
import com.toloknov.summerschool.database.model.toDomain
import com.toloknov.summerschool.database.model.toEntity
import com.toloknov.summerschool.database.utils.TransactionProvider
import com.toloknov.summerschool.domain.api.TodoItemsRepository
import com.toloknov.summerschool.domain.model.ResponseStatus
import com.toloknov.summerschool.domain.model.TodoItem
import com.toloknov.summerschool.network.TodoApi
import com.toloknov.summerschool.network.model.ItemsListTransmitModel
import com.toloknov.summerschool.todoapp.NetworkPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject


class TodoItemsRepositoryImpl @Inject constructor(
    private val dao: TodoDao,
    private val api: TodoApi,
    private val networkDataStore: DataStore<NetworkPreferences>,
    private val transactionProvider: TransactionProvider,
    private val itemListMerger: ItemListMerger
) : TodoItemsRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val atomicDispatcher = Dispatchers.IO.limitedParallelism(1)

    private val _networkRequestStatus: MutableStateFlow<ResponseStatus> =
        MutableStateFlow(ResponseStatus.Idle)

    override fun getActionStatusFlow(): StateFlow<ResponseStatus> = _networkRequestStatus

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
                com.toloknov.summerschool.network.model.ItemTransmitModel(
                    ok = true,
                    element = item.toRest()
                )

            val response = safeRequest { api.addItem(revision, model) }

            if (response != null) {
                updateDataStore(revision + 1)
            }
        }
    }

    override suspend fun updateItem(item: TodoItem) {
        withContext(atomicDispatcher) {
            dao.update(item.toEntity())

            val revision = getCurrentRevision()
            val model =
                com.toloknov.summerschool.network.model.ItemTransmitModel(
                    ok = true,
                    element = item.toRest()
                )

            val response =
                safeRequest { api.updateItemById(id = item.id, revision = revision, body = model) }

            if (response != null) {
                updateDataStore(revision + 1)
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
                    com.toloknov.summerschool.network.model.ItemTransmitModel(
                        ok = true,
                        element = item.toRest()
                    )

                val response = safeRequest {
                    api.updateItemById(
                        id = item.id,
                        revision = revision,
                        body = model
                    )
                }

                if (response != null) {
                    updateDataStore(revision + 1)
                }
            }
        }

    override suspend fun removeItem(itemId: String) {
        withContext(atomicDispatcher) {
            dao.deleteById(itemId)

            val revision = getCurrentRevision()

            val response = safeRequest { api.deleteItemById(id = itemId, revision = revision) }

            if (response != null) {
                updateDataStore(revision + 1)
            }
        }
    }

    override suspend fun syncItems() {
        CoroutineScope(atomicDispatcher).launch {
            val remoteState = safeRequest { api.getAllItems() } ?: return@launch

            remoteState.revision?.let {
                updateDataStore(it)
            }

            val remoteItems = remoteState.list?.map { it.toDomain() } ?: listOf()
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
            dao.upsertAll(resultList.map { it.toEntity() })

            val response = safeRequest { api.updateItems(storeValue.revision, body) }

            response?.revision?.let {
                updateDataStore(revision = it, lastUpdateTime = ZonedDateTime.now())
            }
        }
    }

    override suspend fun syncItemsWithResult(): Result<Unit> {
        return withContext(atomicDispatcher) {

            val remoteState =
                safeRequest { api.getAllItems() } ?: return@withContext Result.failure(
                    Throwable()
                )

            remoteState.revision?.let {
                updateDataStore(it)
            }

            val remoteItems = remoteState.list?.map { it.toDomain() } ?: listOf()
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
            dao.upsertAll(resultList.map { it.toEntity() })

            val response = safeRequest { api.updateItems(storeValue.revision, body) }

            response?.revision?.let {
                updateDataStore(revision = it, lastUpdateTime = ZonedDateTime.now())
                return@withContext Result.success(Unit)
            }
            return@withContext Result.failure(Throwable())
        }
    }

    private suspend fun updateDataStore(revision: Int, lastUpdateTime: ZonedDateTime? = null) {
        networkDataStore.updateData { prefs ->
            val builder = prefs.toBuilder().setRevision(revision)
            lastUpdateTime?.let {
                builder.setLastUpdateTime(ZonedDateTime.now().toEpochSecond())
            }
            builder.build()
        }
    }

    private suspend fun getCurrentRevision(): Int = networkDataStore.data.first().revision

    private suspend fun <T> safeRequest(request: suspend () -> T): T? =
        withContext(atomicDispatcher) {
            _networkRequestStatus.emit(ResponseStatus.InProgress)
            try {
                val response = request()
                _networkRequestStatus.emit(ResponseStatus.Success)
                return@withContext response
            } catch (e: RestException) {
                when (e) {
                    is BadCredentials -> {
                        syncItems()
                        return@withContext safeRequest(request)
                    }

                    is NotAuthorize -> {
                        networkDataStore.updateData { it.toBuilder().setToken("").build() }
                        return@withContext null
                    }

                    is NotFound -> {
                        syncItems()
                        return@withContext safeRequest(request)
                    }

                    is InternalServerError -> {
                        _networkRequestStatus.emit(ResponseStatus.Error)
                        return@withContext null
                    }

                    is UnexpectedRest -> {
                        _networkRequestStatus.emit(ResponseStatus.Error)
                        return@withContext null
                    }
                }
            } catch (e: UnknownHostException) {
                Log.d("TodoItemsRepositoryImpl", "UnknownHostException")
                _networkRequestStatus.emit(ResponseStatus.NetworkUnavailable)
                return@withContext null
            } catch (e: Exception) {
                _networkRequestStatus.emit(ResponseStatus.Error)
                return@withContext null
            }
        }
}