package com.toloknov.summerschool.todoapp.data.remote

import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT


interface TodoApi {
    @GET("")
    suspend fun getAllItems()

    @GET("")
    suspend fun getItemById()

    @PATCH("")
    suspend fun updateItems()

    @POST("")
    suspend fun addItem()

    @PUT("")
    suspend fun updateItemById()

    @DELETE("")
    suspend fun deleteItemById()
}