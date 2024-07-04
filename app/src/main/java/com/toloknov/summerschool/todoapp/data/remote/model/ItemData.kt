package com.toloknov.summerschool.todoapp.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemData(
    @SerialName("id") val id: String, // уникальный идентификатор элемента
    @SerialName("text") val text: String,
    @SerialName("importance") val importance: ItemImportance, // importance = low | basic | important
    @SerialName("deadline") val deadline: Long, // int64, может отсутствовать, тогда нет
    @SerialName("done") val done: Boolean,
    @SerialName("color") val color: String? = null, // может отсутствовать
    @SerialName("created_at") val createdAt: Long,
    @SerialName("changed_at") val changedAt: Long,
    @SerialName("last_updated_by") val lastUpdatedBy: String
)
