package com.toloknov.summerschool.todoapp.data.model

import com.toloknov.summerschool.todoapp.domain.model.ItemImportance
import java.time.ZonedDateTime

data class TodoItemEntity(
    val id: String,
    val text: String,
    val importance: ItemImportance,
    val isDone: Boolean,
    val creationDate: ZonedDateTime,

    val deadlineTs: ZonedDateTime? = null,
    val updateTs: ZonedDateTime? = null
)
