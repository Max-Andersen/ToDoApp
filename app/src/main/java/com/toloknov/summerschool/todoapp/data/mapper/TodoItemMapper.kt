package com.toloknov.summerschool.todoapp.data.mapper

import com.toloknov.summerschool.todoapp.data.model.TodoItemEntity
import com.toloknov.summerschool.todoapp.domain.model.TodoItem

fun TodoItem.toEntity(): TodoItemEntity {
    return TodoItemEntity(
        id = id,
        text = text,
        importance = importance,
        isDone = isDone,
        creationDate = creationDate,
        deadlineTs = deadlineTs,
        updateTs = updateTs,
    )
}

fun TodoItemEntity.toDomain(): TodoItem {
    return TodoItem(
        id = id,
        text = text,
        importance = importance,
        isDone = isDone,
        creationDate = creationDate,
        deadlineTs = deadlineTs,
        updateTs = updateTs,
    )
}