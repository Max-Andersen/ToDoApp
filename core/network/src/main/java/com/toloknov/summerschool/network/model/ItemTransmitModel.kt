package com.toloknov.summerschool.network.model

import com.google.gson.annotations.SerializedName
import com.toloknov.summerschool.network.util.NullableIntSerializer
import kotlinx.serialization.Serializable

@Serializable
data class ItemTransmitModel(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("element") val element: ItemData,
    @SerializedName("revision") @Serializable(with = NullableIntSerializer::class) val revision: Int? = null
)