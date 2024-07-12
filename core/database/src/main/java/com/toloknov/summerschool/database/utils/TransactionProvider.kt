package com.toloknov.summerschool.database.utils

import androidx.room.withTransaction
import com.toloknov.summerschool.database.RoomDb


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