package com.toloknov.summerschool.domain.api

import com.toloknov.summerschool.domain.model.ApplicationTheme
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    fun setTheme(theme: ApplicationTheme)

    fun getTheme(): Flow<ApplicationTheme>
}