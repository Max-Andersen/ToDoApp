package com.toloknov.summerschool.todoapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.toloknov.summerschool.todoapp.ui.card.TodoItemCard
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
            TodoItemsList(
                clickOnItem = {},
                clickOnCreate = {},
            )
        }

        composable(
            route = "card/{itemId}",
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.StringType
                }
            )
        ) {
            TodoItemCard(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}