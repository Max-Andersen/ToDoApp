package com.toloknov.summerschool.todoapp.di

import android.content.Context
import com.toloknov.summerschool.todoapp.data.local.db.RoomDb
import com.toloknov.summerschool.todoapp.data.local.db.dao.TodoDao
import com.toloknov.summerschool.todoapp.data.local.db.utils.TransactionProvider
import com.toloknov.summerschool.todoapp.data.util.ItemListMerger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): RoomDb {
        return RoomDb.databaseBuilder(
            context
        )
    }

    @Provides
    fun provideTodoDao(database: RoomDb): TodoDao {
        return database.todoDao()
    }


    @Provides
    fun provideTransactionProvider(database: RoomDb): TransactionProvider {
        return TransactionProvider(database)
    }

    @Provides
    fun provideListMerger(): ItemListMerger = ItemListMerger()
}