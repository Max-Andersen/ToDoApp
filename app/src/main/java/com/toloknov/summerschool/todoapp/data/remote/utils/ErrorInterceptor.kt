package com.toloknov.summerschool.todoapp.data.remote.utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.Interceptor
import okhttp3.Response

class ErrorInterceptor(private val gson: Gson) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.isSuccessful) {
            return response
        } else {
            val message = response.body?.string()
            Log.d("ErrorInterceptor", message.toString())
            val exception =
                when (response.code) {

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
            response.close()
            throw exception
        }
    }
}