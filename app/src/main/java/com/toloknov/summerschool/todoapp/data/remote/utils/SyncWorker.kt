package com.toloknov.summerschool.todoapp.data.remote.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.toloknov.summerschool.todoapp.TodoApp
import com.toloknov.summerschool.todoapp.di.DIContainer
import java.util.concurrent.TimeUnit

class SyncWorker(
    appContext: Context, workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val diContainer = (applicationContext as TodoApp) as DIContainer
        val repository = diContainer.getTodoItemsRepository()
        repository.syncItems()
        return Result.success()
    }
}