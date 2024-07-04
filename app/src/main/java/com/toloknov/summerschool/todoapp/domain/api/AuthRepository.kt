package com.toloknov.summerschool.todoapp.domain.api

import com.toloknov.summerschool.todoapp.NetworkPreferences


interface AuthRepository {
    suspend fun saveToken(token: String): NetworkPreferences

    suspend fun getToken(): String
}