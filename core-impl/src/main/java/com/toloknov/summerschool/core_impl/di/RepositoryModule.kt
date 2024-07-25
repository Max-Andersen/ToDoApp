package com.toloknov.summerschool.core_impl.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.toloknov.summerschool.core_impl.ItemListMerger
import com.toloknov.summerschool.core_impl.repository.NetworkRepositoryImpl
import com.toloknov.summerschool.core_impl.repository.SyncRepositoryImpl
import com.toloknov.summerschool.core_impl.repository.ThemeRepositoryImpl
import com.toloknov.summerschool.core_impl.repository.TodoItemsRepositoryImpl
import com.toloknov.summerschool.database.dao.TodoDao
import com.toloknov.summerschool.database.utils.TransactionProvider
import com.toloknov.summerschool.domain.api.NetworkRepository
import com.toloknov.summerschool.domain.api.SyncRepository
import com.toloknov.summerschool.domain.api.ThemeRepository
import com.toloknov.summerschool.domain.api.TodoItemsRepository
import com.toloknov.summerschool.network.TodoApi
import com.toloknov.summerschool.todoapp.AppThemePreferences
import com.toloknov.summerschool.todoapp.NetworkPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideListMerger(): ItemListMerger =
        ItemListMerger()

    @Provides
    @Singleton
    fun provideTodoItemsRepository(
        todoApi: TodoApi,
        networkDataStore: DataStore<NetworkPreferences>,
        todoDao: TodoDao,
        transactionProvider: TransactionProvider,
        itemListMerger: ItemListMerger
    ): TodoItemsRepository {
        return TodoItemsRepositoryImpl(
            dao = todoDao,
            api = todoApi,
            networkDataStore = networkDataStore,
            transactionProvider = transactionProvider,
            itemListMerger = itemListMerger
        )
    }

    @Provides
    @Singleton
    fun provideNetworkRepository(
        networkDataStore: DataStore<NetworkPreferences>,
        okHttpClient: OkHttpClient
    ): NetworkRepository {
        return NetworkRepositoryImpl(networkDataStore, okHttpClient)
    }

    @Provides
    @Singleton
    fun provideThemeRepository(
        themeDataStore: DataStore<AppThemePreferences>,
    ): ThemeRepository {
        return ThemeRepositoryImpl(themeDataStore)
    }


    @Provides
    @Singleton
    fun provideSyncRepository(
        @ApplicationContext context: Context,
        todoItemsRepository: TodoItemsRepository
    ): SyncRepository {
        return SyncRepositoryImpl(
            context,
            todoItemsRepository
        )
    }
}