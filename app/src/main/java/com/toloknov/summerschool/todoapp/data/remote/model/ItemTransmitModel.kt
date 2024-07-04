package com.toloknov.summerschool.todoapp.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemTransmitModel(
    @SerialName("ok") val ok: Boolean,
    @SerialName("element") val element: ItemData,
    @SerialName("revision") val revision: Int? = null
)