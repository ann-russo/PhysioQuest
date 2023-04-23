package com.example.physioquest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.physioquest.screen.HomeScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        composable(Screen.Account.route) {
            //TODO
        }

        composable(Screen.Lernmodus.route) {
            //TODO
        }

        composable(Screen.Duellmodus.route) {
            //TODO
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Lernmodus : Screen("lernmodus")
    object Duellmodus : Screen("duellmodus")
    object Account : Screen("account")
}