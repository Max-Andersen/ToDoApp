package com.toloknov.summerschool.todoapp.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
enum class ItemImportance {
    low, basic, important
}