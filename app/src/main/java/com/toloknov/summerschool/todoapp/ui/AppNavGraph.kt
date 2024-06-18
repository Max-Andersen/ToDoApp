package com.toloknov.summerschool.todoapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.toloknov.summerschool.todoapp.ui.list.TodoItemsList

@Composable
fun AppNavGraph(
    startDestination: String,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("list") {
            TodoItemsList()
        }
    }
}