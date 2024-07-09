package com.toloknov.summerschool.todoapp.data.repository

import androidx.datastore.core.DataStore
import com.toloknov.summerschool.todoapp.NetworkPreferences
import com.toloknov.summerschool.todoapp.domain.api.NetworkRepository
import kotlinx.coroutines.flow.first

class NetworkRepositoryImpl(
    private val networkDataStore: DataStore<NetworkPreferences>
) : NetworkRepository {

    override suspend fun saveToken(token: String) {
        networkDataStore.updateData { prefs ->
            prefs.toBuilder().setToken(token).build()
        }
    }

    override suspend fun getToken(): String = networkDataStore.data.first().token

    override suspend fun getRevision(): Int = networkDataStore.data.first().revision

    override suspend fun saveRevision(revision: Int) {
        networkDataStore.updateData { prefs ->
            prefs.toBuilder().setRevision(revision).build()
        }
    }
}