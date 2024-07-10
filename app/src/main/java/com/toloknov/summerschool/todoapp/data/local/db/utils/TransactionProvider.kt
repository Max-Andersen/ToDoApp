package com.toloknov.summerschool.todoapp.data.local.db.utils

import androidx.room.withTransaction
import com.toloknov.summerschool.todoapp.data.local.db.RoomDb


/**
 * Класс, для выполнения действий в рамках одной БД-транзакции
 */
class TransactionProvider(
    private val db: RoomDb
) {
    suspend fun <R> runAsTransaction(block: suspend () -> R): R {
        return db.withTransaction(block)
    }
}