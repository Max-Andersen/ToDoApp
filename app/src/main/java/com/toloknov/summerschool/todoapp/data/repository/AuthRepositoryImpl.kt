package com.toloknov.summerschool.todoapp.data.repository

import androidx.datastore.core.DataStore
import com.toloknov.summerschool.todoapp.NetworkPreferences
import com.toloknov.summerschool.todoapp.domain.api.AuthRepository
import kotlinx.coroutines.flow.first

class AuthRepositoryImpl(
    private val networkDataStore: DataStore<NetworkPreferences>
) : AuthRepository {

    override suspend fun saveToken(token: String) =
        networkDataStore.updateData { prefs ->
            prefs.toBuilder().setToken(token).build()
        }

    override suspend fun getToken(): String = networkDataStore.data.first().token
}