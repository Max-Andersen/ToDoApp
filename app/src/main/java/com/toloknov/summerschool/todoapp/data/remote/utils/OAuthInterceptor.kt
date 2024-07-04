package com.toloknov.summerschool.todoapp.data.remote.utils

import androidx.datastore.core.DataStore
import com.toloknov.summerschool.todoapp.AuthorizationPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class OAuthInterceptor(
    private val authDataStore: DataStore<AuthorizationPreferences>,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val preferences =
            runBlocking {
                authDataStore.data.first()
            }
        val originalRequest = chain.request()
        val originalRequestWithAccessToken =
            originalRequest.newBuilder()
                .appendOAuthToken(preferences.token).build()

        return chain.proceed(originalRequestWithAccessToken)
    }

    private fun Request.Builder.appendOAuthToken(token: String) =
        this.header("Authorization", "OAuth $token")

}