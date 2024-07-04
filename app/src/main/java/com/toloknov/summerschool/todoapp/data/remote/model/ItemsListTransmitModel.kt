package com.toloknov.summerschool.todoapp.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemsListTransmitModel(
    @SerialName("ok") val ok: Boolean,
    @SerialName("list") val list: List<ItemData>? = null,
    @SerialName("revision") val revision: Int? = null
)
