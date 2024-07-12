package com.toloknov.summerschool.todoapp.data.remote.utils

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.atomic.AtomicInteger

class ErrorInterceptor : Interceptor {

    private var retryCount: AtomicInteger = AtomicInteger(0)
    private val maxRetries = 3

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.isSuccessful) {
            return response
        } else {
            val message = response.body?.string()

            val retryRequest = chain.request()
            var retryResponse: Response? = null
            var responseOK = false

            while (!responseOK && retryCount.get() < maxRetries) {
                try {
                    retryResponse = chain.proceed(retryRequest)
                    responseOK = retryResponse.isSuccessful
                } catch (e: Exception) {
                    retryCount.incrementAndGet()
                    if (retryCount.get() >= maxRetries) {
                        break
                    }
                }
            }

            Log.d("ErrorInterceptor", message.toString())
            val exception =
                when (retryResponse?.code) {

                    400 -> {
                        RestException.BadCredentials
                    }

                    401 -> {
                        RestException.NotAuthorize
                    }

                    404 -> {
                        RestException.NotFound
                    }

                    500 -> {
                        RestException.InternalServerError(message?.ifBlank { "Ошибка сервера" })
                    }

                    else -> {
                        RestException.UnexpectedRest(message?.ifBlank { "Пустой ответ" })
                    }
                }
            retryResponse?.close()
            throw exception
        }
    }
}