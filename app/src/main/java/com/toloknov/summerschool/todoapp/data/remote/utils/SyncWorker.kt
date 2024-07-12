package com.toloknov.summerschool.todoapp.data.remote.utils

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.toloknov.summerschool.todoapp.domain.api.TodoItemsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    private val todoItemsRepository: TodoItemsRepository,
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        return when (runAttemptCount) {
            in 0..3 -> {
                val result = todoItemsRepository.syncItemsWithResult()
                if (result.isSuccess) {
                    Result.success()
                } else {
                    Result.retry()
                }
            }

            else -> Result.failure()
        }
    }
}