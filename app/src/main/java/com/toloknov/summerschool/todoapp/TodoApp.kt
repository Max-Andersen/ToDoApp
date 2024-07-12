package com.toloknov.summerschool.todoapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.toloknov.summerschool.domain.api.SyncRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TodoApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory


    override val workManagerConfiguration: Configuration by lazy {
        Configuration.Builder().setWorkerFactory(workerFactory).build()
    }

    @Inject
    lateinit var syncRepository: SyncRepository

    override fun onCreate() {
//        val domainComponent = DaggerCoreImplComponent.factory().create(this)
//        val appComponent = DaggerAppComponent.factory().create(domainComponent)

        super.onCreate()


        syncRepository.configureSync()
    }
}