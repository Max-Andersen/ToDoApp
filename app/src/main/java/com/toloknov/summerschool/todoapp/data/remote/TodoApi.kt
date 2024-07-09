package com.toloknov.summerschool.todoapp.data.remote

import com.toloknov.summerschool.todoapp.data.remote.model.ItemTransmitModel
import com.toloknov.summerschool.todoapp.data.remote.model.ItemsListTransmitModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TodoApi {

    @GET("list")
    suspend fun getAllItems(): Response<ItemsListTransmitModel>

    @GET("list/{id}")
    suspend fun getItemById(
        @Path("id") id: String
    ): Response<ItemTransmitModel>

    @PATCH("list")
    suspend fun updateItems(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body body: ItemsListTransmitModel
    ): Response<ItemsListTransmitModel>

    @POST("list")
    suspend fun addItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body body: ItemTransmitModel
    ): Response<ItemTransmitModel>

    @PUT("list/{id}")
    suspend fun updateItemById(
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") revision: Int,
        @Body body: ItemTransmitModel
    ): Response<ItemTransmitModel>

    @DELETE("list/{id}")
    suspend fun deleteItemById(
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") revision: Int,
    ): Response<ItemTransmitModel>
}