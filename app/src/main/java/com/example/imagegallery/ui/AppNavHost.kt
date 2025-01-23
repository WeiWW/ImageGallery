package com.example.imagegallery.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.imagegallery.GalleryScreen
import com.example.imagegallery.ui.login.LoginScreen


@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Gallery.route) {
        composable(Screen.Gallery.route) { GalleryScreen(navController) }
        composable(Screen.Login.route) { LoginScreen({ navController.navigate(Screen.Gallery.route) }) }

    }
}

sealed class Screen(val route: String) {
    data object Login : Screen("login_screen")
    data object Gallery : Screen("Gallery_screen")
}