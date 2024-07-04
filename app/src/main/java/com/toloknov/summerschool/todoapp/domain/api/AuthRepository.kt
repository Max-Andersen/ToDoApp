package com.toloknov.summerschool.todoapp.domain.api

import com.toloknov.summerschool.todoapp.AuthorizationPreferences

interface AuthRepository {
    suspend fun saveToken(token: String): AuthorizationPreferences

    suspend fun getToken(): String
}