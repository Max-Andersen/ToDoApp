package com.toloknov.summerschool.todoapp.di

import androidx.datastore.core.DataStore
import com.toloknov.summerschool.todoapp.NetworkPreferences
import com.toloknov.summerschool.todoapp.data.remote.TodoApi
import com.toloknov.summerschool.todoapp.data.remote.utils.ErrorInterceptor
import com.toloknov.summerschool.todoapp.data.remote.utils.OAuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit

interface InnerDIDependencies {

    fun getNetworkDataStore(): DataStore<NetworkPreferences>

    fun getOAuthInterceptor(): OAuthInterceptor

    fun getErrorInterceptor(): ErrorInterceptor

    fun getClient(): OkHttpClient

    fun getRetrofit(): Retrofit

    fun getTodoApi(): TodoApi
}