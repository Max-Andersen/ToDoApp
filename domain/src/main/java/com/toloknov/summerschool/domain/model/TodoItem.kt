package com.toloknov.summerschool.domain.model

import java.time.ZonedDateTime

data class TodoItem(
    val id: String,
    val text: String,
    val importance: ItemImportance,
    val isDone: Boolean,
    val creationDate: ZonedDateTime,

    val deadlineTs: ZonedDateTime? = null,
    val updateTs: ZonedDateTime = ZonedDateTime.now()
)
