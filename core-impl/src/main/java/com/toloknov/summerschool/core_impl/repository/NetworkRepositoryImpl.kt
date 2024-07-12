package com.toloknov.summerschool.core_impl.repository

import androidx.datastore.core.DataStore
import com.toloknov.summerschool.domain.api.NetworkRepository
import com.toloknov.summerschool.todoapp.NetworkPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(
    private val networkDataStore: DataStore<NetworkPreferences>
) : NetworkRepository {

    override suspend fun saveToken(token: String) {
        networkDataStore.updateData { prefs ->
            prefs.toBuilder().setToken(token).build()
        }
    }

    override suspend fun getTokenFlow(): Flow<String> = networkDataStore.data.map { it.token }

    override suspend fun getRevision(): Int = networkDataStore.data.first().revision

    override suspend fun saveRevision(revision: Int) {
        networkDataStore.updateData { prefs ->
            prefs.toBuilder().setRevision(revision).build()
        }
    }
}