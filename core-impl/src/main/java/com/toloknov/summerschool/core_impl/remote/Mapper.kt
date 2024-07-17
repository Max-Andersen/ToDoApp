package com.toloknov.summerschool.core_impl.remote

import com.toloknov.summerschool.domain.model.TodoItem
import com.toloknov.summerschool.network.model.ItemData
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import com.toloknov.summerschool.domain.model.ItemImportance as ItemImportanceDomain


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

fun ItemData.toEntity() = com.toloknov.summerschool.database.model.TodoItemEntity(
    id = id,
    text = text,
    importance = importance.toDomain(),
    isDone = done,
    creationDate = ZonedDateTime.ofInstant(
        Instant.ofEpochSecond(createdAt),
        ZoneId.systemDefault()
    ),
    deadlineDate = deadline?.let {
        ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(it),
            ZoneId.systemDefault()
        )
    },
    updateDate = ZonedDateTime.ofInstant(Instant.ofEpochSecond(changedAt), ZoneId.systemDefault()),
)

fun com.toloknov.summerschool.database.model.TodoItemEntity.toRest() =
    ItemData(
        id = id,
        text = text,
        importance = importance.toRest(),
        done = isDone,
        createdAt = creationDate.toEpochSecond(),
        deadline = deadlineDate?.toEpochSecond(),
        changedAt = updateDate?.toEpochSecond() ?: ZonedDateTime.now().toEpochSecond(),
        lastUpdatedBy = "пу пу пу",
    )


fun com.toloknov.summerschool.network.model.ItemImportance.toDomain(): ItemImportanceDomain {
    return when (this) {
        com.toloknov.summerschool.network.model.ItemImportance.low -> ItemImportanceDomain.LOW
        com.toloknov.summerschool.network.model.ItemImportance.basic -> ItemImportanceDomain.COMMON
        com.toloknov.summerschool.network.model.ItemImportance.important -> ItemImportanceDomain.HIGH
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

fun ItemImportanceDomain.toRest(): com.toloknov.summerschool.network.model.ItemImportance {
    return when (this) {
        ItemImportanceDomain.LOW -> com.toloknov.summerschool.network.model.ItemImportance.low
        ItemImportanceDomain.COMMON -> com.toloknov.summerschool.network.model.ItemImportance.basic
        ItemImportanceDomain.HIGH -> com.toloknov.summerschool.network.model.ItemImportance.important
    }
}