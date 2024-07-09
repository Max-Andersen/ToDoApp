package com.toloknov.summerschool.todoapp.di

import com.toloknov.summerschool.todoapp.domain.api.NetworkRepository
import com.toloknov.summerschool.todoapp.domain.api.TodoItemsRepository

interface DIContainer {

    fun getTodoItemsRepository(): TodoItemsRepository

    fun getAuthRepository(): NetworkRepository

}