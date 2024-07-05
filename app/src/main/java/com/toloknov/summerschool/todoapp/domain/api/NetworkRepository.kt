package com.toloknov.summerschool.todoapp.domain.api

import com.toloknov.summerschool.todoapp.NetworkPreferences


interface NetworkRepository {
    suspend fun saveToken(token: String)

    suspend fun getToken(): String

    suspend fun getRevision(): Int

    suspend fun saveRevision(revision: Int)
}