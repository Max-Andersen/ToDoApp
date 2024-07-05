package com.toloknov.summerschool.todoapp.data.remote.model

import com.toloknov.summerschool.todoapp.data.local.db.model.TodoItemEntity
import com.toloknov.summerschool.todoapp.domain.model.TodoItem
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import com.toloknov.summerschool.todoapp.domain.model.ItemImportance as ItemImportanceDomain


fun ItemsListTransmitModel.toListDomain() = this.list?.map {
    it.toDomain()
}


fun ItemData.toDomain() = TodoItem(
    id = id,
    text = text,
    importance = importance.toDomain(),
    isDone = done,
    creationDate = ZonedDateTime.ofInstant(
        Instant.ofEpochSecond(createdAt),
        ZoneId.systemDefault()
    ),
    deadlineTs = deadline?.let {
        ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(it),
            ZoneId.systemDefault()
        )
    },
    updateTs = ZonedDateTime.ofInstant(Instant.ofEpochSecond(changedAt), ZoneId.systemDefault()),
)

fun ItemData.toEntity() = TodoItemEntity(
    id = id,
    text = text,
    importance = importance.toDomain(),
    isDone = done,
    creationDate = ZonedDateTime.ofInstant(
        Instant.ofEpochSecond(createdAt),
        ZoneId.systemDefault()
    ),
    deadlineTs = deadline?.let {
        ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(it),
            ZoneId.systemDefault()
        )
    },
    updateTs = ZonedDateTime.ofInstant(Instant.ofEpochSecond(changedAt), ZoneId.systemDefault()),
)

fun TodoItemEntity.toRest() = ItemData(
    id = id,
    text = text,
    importance = importance.toRest(),
    done = isDone,
    createdAt = creationDate.toEpochSecond(),
    deadline = deadlineTs?.toEpochSecond(),
    changedAt = updateTs?.toEpochSecond() ?: ZonedDateTime.now().toEpochSecond(),
    lastUpdatedBy = "пу пу пу",
)


fun ItemImportance.toDomain(): ItemImportanceDomain {
    return when (this) {
        ItemImportance.low -> ItemImportanceDomain.LOW
        ItemImportance.basic -> ItemImportanceDomain.COMMON
        ItemImportance.important -> ItemImportanceDomain.HIGH
    }
}


fun TodoItem.toRest() = ItemData(
    id = id,
    text = text,
    importance = importance.toRest(),
    createdAt = creationDate.toEpochSecond(),
    deadline = deadlineTs?.toEpochSecond(),
    color = "#FFFFFF",
    changedAt = updateTs?.toEpochSecond() ?: ZonedDateTime.now().toEpochSecond(),
    done = isDone,
    lastUpdatedBy = "пу пу пу"
)

fun ItemImportanceDomain.toRest(): ItemImportance {
    return when (this) {
        ItemImportanceDomain.LOW -> ItemImportance.low
        ItemImportanceDomain.COMMON -> ItemImportance.basic
        ItemImportanceDomain.HIGH -> ItemImportance.important
    }
}