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
            val bodyJson = response.body?.string()
            val errorMessage =
                try {
                    gson.fromJson(bodyJson, ExceptionJson::class.java).message
                } catch (e: JsonSyntaxException) {
                    Log.d("JsonParseException", "${e.message}")
                    null
                } ?: "JsonParseException! Body: $bodyJson"
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
                        RestException.InternalServerError(errorMessage.ifBlank { "Ошибка сервера" })
                    }

                    else -> {
                        RestException.UnexpectedRest(errorMessage.ifBlank { "Пустой ответ" })
                    }
                }
            response.close()
            throw exception
        }
    }
}