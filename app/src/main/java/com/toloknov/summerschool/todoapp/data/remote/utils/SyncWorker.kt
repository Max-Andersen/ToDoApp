package com.toloknov.summerschool.todoapp.data.remote.utils

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.toloknov.summerschool.todoapp.domain.api.TodoItemsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    private val todoItemsRepository: TodoItemsRepository,
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        todoItemsRepository.syncItems()
        return Result.success()
    }
}