package com.toloknov.summerschool.todoapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.toloknov.summerschool.todoapp.NetworkPreferences
import com.toloknov.summerschool.todoapp.data.local.db.dao.TodoDao
import com.toloknov.summerschool.todoapp.data.local.db.utils.TransactionProvider
import com.toloknov.summerschool.todoapp.data.remote.TodoApi
import com.toloknov.summerschool.todoapp.data.repository.NetworkRepositoryImpl
import com.toloknov.summerschool.todoapp.data.repository.SyncRepositoryImpl
import com.toloknov.summerschool.todoapp.data.repository.TodoItemsRepositoryImpl
import com.toloknov.summerschool.todoapp.data.util.ItemListMerger
import com.toloknov.summerschool.todoapp.domain.api.NetworkRepository
import com.toloknov.summerschool.todoapp.domain.api.SyncRepository
import com.toloknov.summerschool.todoapp.domain.api.TodoItemsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

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
        networkDataStore: DataStore<NetworkPreferences>
    ): NetworkRepository {
        return NetworkRepositoryImpl(networkDataStore)
    }


    @Provides
    @Singleton
    fun provideSyncRepository(
        @ApplicationContext context: Context,
        todoItemsRepository: TodoItemsRepository
    ): SyncRepository {
        return SyncRepositoryImpl(context, todoItemsRepository)
    }
}