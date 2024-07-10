package com.toloknov.summerschool.todoapp.data.local.db.model

import com.toloknov.summerschool.todoapp.domain.model.TodoItem

fun TodoItem.toEntity(): TodoItemEntity {
    return TodoItemEntity(
        id = id,
        text = text,
        importance = importance,
        isDone = isDone,
        creationDate = creationDate,
        deadlineDate = deadlineTs,
        updateDate = updateTs,
    )
}

fun TodoItemEntity.toDomain(): TodoItem {
    return TodoItem(
        id = id,
        text = text,
        importance = importance,
        isDone = isDone,
        creationDate = creationDate,
        deadlineTs = deadlineDate,
        updateTs = updateDate,
    )
}