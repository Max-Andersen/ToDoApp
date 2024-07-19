package com.toloknov.summerschool.core_impl.repository

import android.util.Log
import androidx.datastore.core.DataStore
import com.toloknov.summerschool.core_impl.remote.utils.RestException
import com.toloknov.summerschool.domain.api.NetworkRepository
import com.toloknov.summerschool.todoapp.NetworkPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.net.UnknownHostException
import javax.inject.Inject


class NetworkRepositoryImpl @Inject constructor(
    private val networkDataStore: DataStore<NetworkPreferences>,
    private val okHttpClient: OkHttpClient
) : NetworkRepository {


    private val atomicDispatcher = Dispatchers.IO

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

    override suspend fun getAvatarId(): String? {
        val urlBuilder = "https://login.yandex.ru/info".toHttpUrlOrNull()!!.newBuilder()
        urlBuilder.addQueryParameter("format", "json")

        val url = urlBuilder.build().toString()

        val request: Request = Request.Builder()
            .url(url)
            .build()
        val call: Call = okHttpClient.newCall(request)
        val response: Response? = safeRequest() { call.execute() }

        val id = response?.body?.string()?.substringAfter("\"default_avatar_id\": \"")?.substringBefore("\"")
        Log.d("TodoItemsRepositoryImpl", "getAvatarId: $id")
        return id
    }

    private suspend fun <T> safeRequest(request: suspend () -> T): T? =
        withContext(atomicDispatcher) {
            try {
                val response = request()
                return@withContext response
            } catch (e: RestException) {
                return@withContext null
            } catch (e: UnknownHostException) {
                Log.d("TodoItemsRepositoryImpl", "UnknownHostException")
                return@withContext null
            } catch (e: Exception) {
                return@withContext null
            }
        }


}