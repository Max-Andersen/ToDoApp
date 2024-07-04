package com.toloknov.summerschool.todoapp.data.repository

import androidx.datastore.core.DataStore
import com.toloknov.summerschool.todoapp.AuthorizationPreferences
import com.toloknov.summerschool.todoapp.domain.api.AuthRepository
import kotlinx.coroutines.flow.first

class AuthRepositoryImpl(
    private val oauthDataStore: DataStore<AuthorizationPreferences>
) : AuthRepository {

    override suspend fun saveToken(token: String) =
        oauthDataStore.updateData { prefs ->
            prefs.toBuilder().setToken(token).build()
        }

    override suspend fun getToken(): String = oauthDataStore.data.first().token
}