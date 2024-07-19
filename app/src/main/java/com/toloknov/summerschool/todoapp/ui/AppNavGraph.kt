package com.toloknov.summerschool.todoapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.toloknov.summerschool.todoapp.ui.main.MainViewModel
import com.toloknov.summerschool.todoapp.ui.navigation.AppScreen
import com.toloknov.summerschool.todoapp.ui.settings.SettingsScreen
import com.toloknov.summerschool.todoapp.ui.settings.SettingsViewModel

@Composable
fun AppNavGraph(
    startDestination: MainViewModel.StartDestination,
    navController: NavHostController
) {
    val startAppScreen = remember(startDestination) {
        when (startDestination) {
            is MainViewModel.StartDestination.LOGIN -> AppScreen.Login
            is MainViewModel.StartDestination.LIST -> AppScreen.List
        }
    }

    NavHost(
        navController = navController,
        startDestination = startAppScreen.route
    ) {

        composable(AppScreen.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                loginSuccess = { navController.navigate(AppScreen.List.route) },
                tokenSpoiled = (startDestination as? MainViewModel.StartDestination.LOGIN)?.tokenSpoiled
                    ?: false
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
                clickOnSettings = {
                    navController.navigate(AppScreen.Settings.route)
                }
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

        composable(
            route = AppScreen.Settings.route
        ) {
            val viewModel = hiltViewModel<SettingsViewModel>()

            SettingsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}