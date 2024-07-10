package com.toloknov.summerschool.todoapp.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.toloknov.summerschool.todoapp.ui.AppNavGraph
import com.toloknov.summerschool.todoapp.ui.common.theme.ToDoAppTheme
import com.toloknov.summerschool.todoapp.ui.navigation.AppScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var splashScreen: SplashScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()

            startDestination?.let { destination ->
                val startDestinationRoute = when (destination) {
                    MainViewModel.StartDestination.LOGIN -> AppScreen.Login.route
                    MainViewModel.StartDestination.LIST -> AppScreen.List.route
                }
                ToDoAppTheme {
                    AppNavGraph(
                        startDestination = startDestinationRoute,
                        navController = rememberNavController()
                    )
                }
            }
        }
    }
}