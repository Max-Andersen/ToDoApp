package com.toloknov.summerschool.todoapp.data.remote.model

import com.google.gson.annotations.SerializedName
import com.toloknov.summerschool.todoapp.data.remote.utils.NullableIntSerializer
import kotlinx.serialization.Serializable

@Serializable
data class ItemsListTransmitModel(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("list") val list: List<ItemData>? = null,
    @SerializedName("revision") @Serializable(with = NullableIntSerializer::class) val revision: Int? = null
)
