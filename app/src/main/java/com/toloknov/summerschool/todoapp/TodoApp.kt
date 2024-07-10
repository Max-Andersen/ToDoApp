package com.toloknov.summerschool.todoapp

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.toloknov.summerschool.todoapp.data.remote.utils.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class TodoApp : Application(), Configuration.Provider {


    @Inject
    lateinit var workerFactory: HiltWorkerFactory


    override val workManagerConfiguration: Configuration by lazy {
        Configuration.Builder().setWorkerFactory(workerFactory).build()
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

                // Todo sync
            }
        }

        val connectivityManager =
            getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

}