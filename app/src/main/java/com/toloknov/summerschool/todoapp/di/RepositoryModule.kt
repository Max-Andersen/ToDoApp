package com.toloknov.summerschool.todoapp.di

import androidx.datastore.core.DataStore
import com.toloknov.summerschool.todoapp.NetworkPreferences
import com.toloknov.summerschool.todoapp.data.remote.TodoApi
import com.toloknov.summerschool.todoapp.data.repository.NetworkRepositoryImpl
import com.toloknov.summerschool.todoapp.data.repository.TodoItemsRepositoryImpl
import com.toloknov.summerschool.todoapp.domain.api.NetworkRepository
import com.toloknov.summerschool.todoapp.domain.api.TodoItemsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTodoItemsRepository(
        todoApi: TodoApi,
        networkDataStore: DataStore<NetworkPreferences>
//        todoDao: TodoDao
    ): TodoItemsRepository {
        return TodoItemsRepositoryImpl(todoApi,networkDataStore)
    }

    @Provides
    @Singleton
    fun provideNetworkRepository(
        networkDataStore: DataStore<NetworkPreferences>
    ): NetworkRepository {
        return NetworkRepositoryImpl(networkDataStore)
    }



}