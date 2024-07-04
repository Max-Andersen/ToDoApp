package com.toloknov.summerschool.todoapp.ui.navigation

sealed class AppScreen(val route: String) {

    data object List : AppScreen(route = "list")

    data class ItemCard(val itemId: String? = null) : AppScreen("itemCard") {
        @JvmName("getRouteToScreen")
        fun getRoute() = this.route + "/${itemId}"
        fun getMask() = this.route + "/{itemId}"
    }

    data object Login : AppScreen("login")
}