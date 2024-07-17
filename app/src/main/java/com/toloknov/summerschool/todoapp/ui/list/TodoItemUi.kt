package com.toloknov.summerschool.todoapp.ui.list

import com.toloknov.summerschool.domain.model.ItemImportance
import com.toloknov.summerschool.domain.model.TodoItem
import com.toloknov.summerschool.todoapp.ui.common.utils.convertToReadable

data class TodoItemUi(
    val id: String,
    val text: String,
    val importance: ItemImportance,
    val isDone: Boolean,
    val creationDate: String,

    val deadlineTs: String? = null,
    val updateTs: String? = null
)


fun TodoItem.toUiModel() = TodoItemUi(
    id = id,
    text = text,
    importance = importance,
    isDone = isDone,
    creationDate = creationDate.convertToReadable() ?: "",
    deadlineTs = deadlineTs?.convertToReadable(),
    updateTs = updateTs?.convertToReadable()
)
