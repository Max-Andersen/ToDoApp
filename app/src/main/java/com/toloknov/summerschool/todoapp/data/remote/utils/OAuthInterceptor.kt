package com.toloknov.summerschool.todoapp.data.remote.utils

import androidx.datastore.core.DataStore
import com.toloknov.summerschool.todoapp.NetworkPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class OAuthInterceptor(
    private val networkDataStore: DataStore<NetworkPreferences>,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val preferences =
            runBlocking {
                networkDataStore.data.first()
            }

        var request = chain.request()

        if (preferences.token.isNotBlank()) {
            request = request.newBuilder()
                .appendOAuthToken(preferences.token).build()
        } else{
            throw IOException("Missing OAuth token")
        }

        return chain.proceed(request)
    }

    private fun Request.Builder.appendOAuthToken(token: String) =
        this.header("Authorization", "OAuth $token")

}