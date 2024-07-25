package com.toloknov.summerschool.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.toloknov.summerschool.todoapp.AppThemePreferences
import com.toloknov.summerschool.todoapp.NetworkPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


private const val NETWORK_PREFERENCES = "network_prefs.pb"
private const val APP_THEME_PREFERENCES = "app_theme_prefs.pb"

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun provideNetworkDataStore(@ApplicationContext appContext: Context): DataStore<NetworkPreferences> {
        return DataStoreFactory.create(
            serializer = NetworkPreferencesSerializer(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.dataStoreFile(NETWORK_PREFERENCES) },
        )
    }

    @Singleton
    @Provides
    fun provideAppThemeDataStore(@ApplicationContext appContext: Context): DataStore<AppThemePreferences> {
        return DataStoreFactory.create(
            serializer = AppThemePreferencesSerializer(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.dataStoreFile(APP_THEME_PREFERENCES) },
        )
    }

}