package com.toloknov.summerschool.todoapp.domain.api


interface NetworkRepository {
    suspend fun saveToken(token: String)

    suspend fun getToken(): String

    suspend fun getRevision(): Int

    suspend fun saveRevision(revision: Int)
}