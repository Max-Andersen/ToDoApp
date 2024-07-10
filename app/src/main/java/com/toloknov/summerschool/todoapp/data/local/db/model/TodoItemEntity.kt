package com.toloknov.summerschool.todoapp.data.local.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.toloknov.summerschool.todoapp.domain.model.ItemImportance
import java.time.ZonedDateTime
@Entity(
    tableName = "todo_item",
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["is_done"]),
    ]
)
data class TodoItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "importance")
    val importance: ItemImportance,
    @ColumnInfo(name = "is_done")
    val isDone: Boolean,
    @ColumnInfo(name = "creation_date")
    val creationDate: ZonedDateTime,

    @ColumnInfo(name = "deadline_date")
    val deadlineDate: ZonedDateTime? = null,
    @ColumnInfo(name = "update_date")
    val updateDate: ZonedDateTime
)
