package com.toloknov.summerschool.network

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
    suspend fun getAllItems(): com.toloknov.summerschool.network.model.ItemsListTransmitModel

    @GET("list/{id}")
    suspend fun getItemById(
        @Path("id") id: String
    ): com.toloknov.summerschool.network.model.ItemTransmitModel

    @PATCH("list")
    suspend fun updateItems(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body body: com.toloknov.summerschool.network.model.ItemsListTransmitModel
    ): com.toloknov.summerschool.network.model.ItemsListTransmitModel

    @POST("list")
    suspend fun addItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body body: com.toloknov.summerschool.network.model.ItemTransmitModel
    ): com.toloknov.summerschool.network.model.ItemTransmitModel

    @PUT("list/{id}")
    suspend fun updateItemById(
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") revision: Int,
        @Body body: com.toloknov.summerschool.network.model.ItemTransmitModel
    ): com.toloknov.summerschool.network.model.ItemTransmitModel

    @DELETE("list/{id}")
    suspend fun deleteItemById(
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") revision: Int,
    ): com.toloknov.summerschool.network.model.ItemTransmitModel
}