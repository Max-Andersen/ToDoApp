package com.toloknov.summerschool.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ItemData(
    @SerializedName("id") val id: String, // уникальный идентификатор элемента
    @SerializedName("text") val text: String,
    @SerializedName("importance") val importance: ItemImportance, // importance = low | basic | important
    @SerializedName("deadline") val deadline: Long?, // int64, может отсутствовать, тогда нет
    @SerializedName("done") val done: Boolean,
    @SerializedName("color")  val color: String? = null, // может отсутствовать
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("changed_at") val changedAt: Long,
    @SerializedName("last_updated_by") val lastUpdatedBy: String
)
