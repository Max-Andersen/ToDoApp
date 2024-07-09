package com.toloknov.summerschool.todoapp.data.remote.model

import com.google.gson.annotations.SerializedName
import com.toloknov.summerschool.todoapp.data.remote.utils.NullableIntSerializer
import kotlinx.serialization.Serializable

@Serializable
data class ItemTransmitModel(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("element") val element: ItemData,
    @SerializedName("revision") @Serializable(with = NullableIntSerializer::class) val revision: Int? = null
)