package com.toloknov.summerschool.todoapp

import com.toloknov.summerschool.core_impl.CoreImplComponent
import dagger.Component

@Component(dependencies = [CoreImplComponent::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(coreImplComponent: CoreImplComponent): AppComponent
    }
}