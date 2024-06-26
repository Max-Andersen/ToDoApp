package com.toloknov.summerschool.todoapp

import android.app.Application
import com.toloknov.summerschool.todoapp.data.repository.TodoItemsRepositoryImpl
import com.toloknov.summerschool.todoapp.domain.api.TodoItemsRepository

class TodoApp : Application(), DiContainer {

    private lateinit var todoItemsRepository: TodoItemsRepository


    override fun onCreate() {
        super.onCreate()
        todoItemsRepository = TodoItemsRepositoryImpl()
    }


    override fun getTodoItemsRepository(): TodoItemsRepository = todoItemsRepository

}

interface DiContainer {

    fun getTodoItemsRepository(): TodoItemsRepository
}