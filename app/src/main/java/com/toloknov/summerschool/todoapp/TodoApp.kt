package com.toloknov.summerschool.todoapp

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.google.gson.Gson
import com.toloknov.summerschool.todoapp.data.local.datastore.NetworkPreferencesSerializer
import com.toloknov.summerschool.todoapp.data.remote.TodoApi
import com.toloknov.summerschool.todoapp.data.remote.utils.ErrorInterceptor
import com.toloknov.summerschool.todoapp.data.remote.utils.OAuthInterceptor
import com.toloknov.summerschool.todoapp.data.repository.AuthRepositoryImpl
import com.toloknov.summerschool.todoapp.data.repository.TodoItemsRepositoryImpl
import com.toloknov.summerschool.todoapp.di.DIContainer
import com.toloknov.summerschool.todoapp.di.InnerDIDependencies
import com.toloknov.summerschool.todoapp.domain.api.AuthRepository
import com.toloknov.summerschool.todoapp.NetworkPreferences
import com.toloknov.summerschool.todoapp.domain.api.TodoItemsRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class TodoApp : Application(), DIContainer, InnerDIDependencies {

    private val networkDatastore: DataStore<NetworkPreferences> by dataStore(
        fileName = "network_prefs.pb",
        serializer = NetworkPreferencesSerializer(),
    )

    private lateinit var todoItemsRepository: TodoItemsRepository
    private lateinit var authRepository: AuthRepository
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
        authRepository = AuthRepositoryImpl(
            this.getNetworkDataStore()
        )

        initInternetAvailableWork()
    }


    private fun initInternet() {
        initUtils()
        initClient()
        initRetrofit()
    }

    private fun initInternetAvailableWork() {
        // Интернет через Wi-fi или мобильную связь
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Если интернет появился, триггерим синхронизацию
                super.onAvailable(network)
                this@TodoApp.getTodoItemsRepository().syncItems()
            }
        }

        val connectivityManager =
            getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    private fun initUtils() {
        oAuthInterceptor = OAuthInterceptor(this.getNetworkDataStore())
        errorInterceptor = ErrorInterceptor(gson)
    }

    private fun initRetrofit() {
        var serverUrl = "https://hive.mrdekk.ru/todo/"

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

    override fun getAuthRepository(): AuthRepository = authRepository

    override fun getNetworkDataStore(): DataStore<NetworkPreferences> = networkDatastore

    override fun getOAuthInterceptor(): OAuthInterceptor = oAuthInterceptor

    override fun getErrorInterceptor(): ErrorInterceptor = errorInterceptor

    override fun getClient(): OkHttpClient = client

    override fun getRetrofit(): Retrofit = retrofit

    override fun getTodoApi(): TodoApi = this.retrofit.create(TodoApi::class.java)
}