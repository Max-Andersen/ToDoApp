package com.toloknov.summerschool.todoapp.domain.api

import kotlinx.coroutines.flow.Flow


interface NetworkRepository {
    suspend fun saveToken(token: String)

    suspend fun getTokenFlow(): Flow<String>

    suspend fun getRevision(): Int

    suspend fun saveRevision(revision: Int)
}