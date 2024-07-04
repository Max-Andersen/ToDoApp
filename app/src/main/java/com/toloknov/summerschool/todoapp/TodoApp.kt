package com.toloknov.summerschool.todoapp

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.google.gson.Gson
import com.toloknov.summerschool.todoapp.data.local.datastore.AuthorizationPreferencesSerializer
import com.toloknov.summerschool.todoapp.data.remote.TodoApi
import com.toloknov.summerschool.todoapp.data.remote.utils.ErrorInterceptor
import com.toloknov.summerschool.todoapp.data.remote.utils.OAuthInterceptor
import com.toloknov.summerschool.todoapp.data.repository.TodoItemsRepositoryImpl
import com.toloknov.summerschool.todoapp.domain.api.TodoItemsRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

class TodoApp : Application(), DIContainer, InnerDIDependencies {

    private val oauthDataStore: DataStore<AuthorizationPreferences> by dataStore(
        fileName = "auth_prefs.pb",
        serializer = AuthorizationPreferencesSerializer(),
    )

    private lateinit var todoItemsRepository: TodoItemsRepository
    private lateinit var oAuthInterceptor: OAuthInterceptor
    private lateinit var errorInterceptor: ErrorInterceptor
    private lateinit var retrofit: Retrofit
    private lateinit var client: OkHttpClient

    private val gson = Gson()


    override fun onCreate() {
        super.onCreate()
        initInternet()

        todoItemsRepository = TodoItemsRepositoryImpl(
            this.getTodoApi()
        )
    }


    private fun initInternet() {
        initUtils()
        initClient()
        initRetrofit()
    }

    private fun initUtils() {
        oAuthInterceptor = OAuthInterceptor(this.getOAuthDataStore())
        errorInterceptor = ErrorInterceptor(gson)
    }

    private fun initRetrofit() {
        var serverUrl = ""

        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(serverUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))

        retrofit = retrofitBuilder.client(this.getClient()).build()
    }

    private fun initClient() {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val clientBuilder: OkHttpClient.Builder =
            OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .callTimeout(120, TimeUnit.SECONDS)

        val oAuthInterceptor = this.getOAuthInterceptor()
        val errorInterceptor = this.getErrorInterceptor()

        client = clientBuilder
            .addInterceptor(oAuthInterceptor)
            .addInterceptor(errorInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    override fun getTodoItemsRepository(): TodoItemsRepository = todoItemsRepository

    override fun getOAuthDataStore(): DataStore<AuthorizationPreferences> = oauthDataStore

    override fun getOAuthInterceptor(): OAuthInterceptor = oAuthInterceptor

    override fun getErrorInterceptor(): ErrorInterceptor = errorInterceptor

    override fun getClient(): OkHttpClient = client

    override fun getRetrofit(): Retrofit = retrofit

    override fun getTodoApi(): TodoApi = this.retrofit.create(TodoApi::class.java)
}

interface DIContainer {

    fun getTodoItemsRepository(): TodoItemsRepository

}

interface InnerDIDependencies {

    fun getOAuthDataStore(): DataStore<AuthorizationPreferences>

    fun getOAuthInterceptor(): OAuthInterceptor

    fun getErrorInterceptor(): ErrorInterceptor

    fun getClient(): OkHttpClient

    fun getRetrofit(): Retrofit

    fun getTodoApi(): TodoApi
}