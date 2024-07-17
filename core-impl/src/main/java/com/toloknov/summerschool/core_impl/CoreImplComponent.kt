package com.toloknov.summerschool.core_impl

import android.content.Context
import com.toloknov.summerschool.core_impl.di.NetworkModule
import com.toloknov.summerschool.core_impl.di.RepositoryModule
import dagger.BindsInstance
import dagger.Component

@Component(modules = [NetworkModule::class, RepositoryModule::class])
interface CoreImplComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): CoreImplComponent
    }
}