package com.toloknov.summerschool.todoapp.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.toloknov.summerschool.todoapp.ui.card.TodoItemCard
import com.toloknov.summerschool.todoapp.ui.card.TodoItemCardViewModel
import com.toloknov.summerschool.todoapp.ui.list.TodoItemsList
import com.toloknov.summerschool.todoapp.ui.list.TodoItemsListViewModel
import com.toloknov.summerschool.todoapp.ui.login.LoginScreen
import com.toloknov.summerschool.todoapp.ui.login.LoginViewModel
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
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                loginSuccess = { navController.navigate(AppScreen.List.route) }
            )
        }

        composable(AppScreen.List.route) {

            val viewModel = hiltViewModel<TodoItemsListViewModel>()

            TodoItemsList(
                viewModel = viewModel,
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
            val viewModel = hiltViewModel<TodoItemCardViewModel>()

            TodoItemCard(
                onBackClick = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}