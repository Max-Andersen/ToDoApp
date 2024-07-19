package com.toloknov.summerschool.core_impl.repository

import androidx.datastore.core.DataStore
import com.toloknov.summerschool.domain.api.ThemeRepository
import com.toloknov.summerschool.todoapp.AppThemePreferences
import com.toloknov.summerschool.todoapp.ApplicationThemeEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.toloknov.summerschool.domain.model.ApplicationTheme as ApplicationThemeDomain


class ThemeRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<AppThemePreferences>
) : ThemeRepository {

    private fun ApplicationThemeEnum.toDomain() = when (this) {
        ApplicationThemeEnum.LIGHT -> ApplicationThemeDomain.LIGHT
        ApplicationThemeEnum.DARK -> ApplicationThemeDomain.DARK
        ApplicationThemeEnum.SYSTEM -> ApplicationThemeDomain.SYSTEM
        ApplicationThemeEnum.UNRECOGNIZED -> ApplicationThemeDomain.SYSTEM
    }

    private fun ApplicationThemeDomain.toData() = when (this) {
        ApplicationThemeDomain.LIGHT -> ApplicationThemeEnum.LIGHT
        ApplicationThemeDomain.DARK -> ApplicationThemeEnum.DARK
        ApplicationThemeDomain.SYSTEM -> ApplicationThemeEnum.SYSTEM
    }

    override fun getTheme(): Flow<ApplicationThemeDomain> =
        dataStore.data.map { it.theme.toDomain() }

    override fun setTheme(theme: ApplicationThemeDomain) {
        CoroutineScope(Dispatchers.Default).launch {
            dataStore.updateData {
                it.toBuilder().setTheme(theme.toData()).build()
            }
        }
    }
}