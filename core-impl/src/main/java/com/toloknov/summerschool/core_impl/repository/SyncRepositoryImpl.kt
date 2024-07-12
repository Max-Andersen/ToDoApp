package com.toloknov.summerschool.core_impl.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.toloknov.summerschool.core_impl.remote.worker.SyncWorker
import com.toloknov.summerschool.domain.api.SyncRepository
import com.toloknov.summerschool.domain.api.TodoItemsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val applicationContext: Context,
    private val todoItemsRepository: TodoItemsRepository
) : SyncRepository {

    override fun configureSync() {
        schedulePeriodicWork()
        initInternetAvailableWork()
    }

    private fun schedulePeriodicWork() {
        // Создаем ограничения
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Создаем запрос на периодическую работу
        // Api WorkManager не обещает выполнить ровно через 8 часов, но обещается когда-нибудь точно выполнить
        val workRequest = PeriodicWorkRequestBuilder<SyncWorker>(8, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        // Запускаем работу
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "SyncPeriodicWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
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

                CoroutineScope(Dispatchers.IO).launch {
                    todoItemsRepository.syncItems()
                }
            }
        }

        val connectivityManager =
            getSystemService(
                applicationContext,
                ConnectivityManager::class.java
            ) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }
}