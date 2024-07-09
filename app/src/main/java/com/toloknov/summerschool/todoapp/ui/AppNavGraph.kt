package com.toloknov.summerschool.todoapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.toloknov.summerschool.todoapp.ui.card.TodoItemCard
import com.toloknov.summerschool.todoapp.ui.list.TodoItemsList
import com.toloknov.summerschool.todoapp.ui.login.LoginScreen
import com.toloknov.summerschool.todoapp.ui.navigation.AppScreen

@Composable
fun AppNavGraph(
    startDestination: String,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(AppScreen.Login.route) {
            LoginScreen(
                loginSuccess = { navController.navigate(AppScreen.List.route) }
            )
        }

        composable(AppScreen.List.route) {
            TodoItemsList(
                clickOnItem = { itemId ->
                    navController.navigate(AppScreen.ItemCard(itemId).getRoute())
                },
                clickOnCreate = {
                    navController.navigate(AppScreen.ItemCard().getRoute())
                },
            )
        }

        composable(
            route = AppScreen.ItemCard().getMask(),
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            TodoItemCard(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}