package com.toloknov.summerschool.core_impl.di

import com.google.gson.Gson
import com.toloknov.summerschool.core_impl.remote.utils.ErrorInterceptor
import com.toloknov.summerschool.core_impl.remote.utils.OAuthInterceptor
import com.toloknov.summerschool.domain.api.NetworkRepository
import com.toloknov.summerschool.network.TodoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOAuthInterceptor(
        networkRepository: NetworkRepository
    ): OAuthInterceptor =
        OAuthInterceptor(networkRepository)

    @Provides
    @Singleton
    fun provideErrorInterceptor(): ErrorInterceptor =
        ErrorInterceptor()

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        oauthInterceptor: OAuthInterceptor,
        errorInterceptor: ErrorInterceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .callTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(oauthInterceptor)
            .addInterceptor(errorInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("https://hive.mrdekk.ru/todo/")
            .build()
    }

    @Provides
    @Singleton
    fun provideTodoApi(retrofit: Retrofit): TodoApi {
        return retrofit.create(TodoApi::class.java)
    }
}