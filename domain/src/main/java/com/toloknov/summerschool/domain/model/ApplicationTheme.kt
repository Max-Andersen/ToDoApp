package com.toloknov.summerschool.domain.model

enum class ApplicationTheme(val nameRu: String) {
    LIGHT("Светлая"),
    DARK("Темная"),
    SYSTEM("Как в системе");


    companion object {
        fun fromNameRu(name: String): ApplicationTheme {
            return when (name) {
                "Светлая" -> ApplicationTheme.LIGHT
                "Темная" -> ApplicationTheme.DARK
                else -> ApplicationTheme.SYSTEM
            }
        }
    }
}