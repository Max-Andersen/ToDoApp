package com.toloknov.summerschool.core_impl.remote.utils

import com.toloknov.summerschool.domain.api.NetworkRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class OAuthInterceptor(
    private val networkRepository: NetworkRepository,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token =
            runBlocking {
                networkRepository.getTokenFlow().first()
            }

        var request = chain.request()

        if (token.isNotBlank()) {
            request = request.newBuilder()
                .appendOAuthToken(token).build()
        } else{
            throw IOException("Missing OAuth token")
        }

        return chain.proceed(request)
    }

    private fun Request.Builder.appendOAuthToken(token: String) =
        this.header("Authorization", "OAuth $token")

}