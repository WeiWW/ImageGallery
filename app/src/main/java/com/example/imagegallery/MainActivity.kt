package com.example.imagegallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.imagegallery.ui.AppNavHost
import com.example.imagegallery.ui.Screen
import com.example.imagegallery.ui.theme.ImageGalleryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        enableEdgeToEdge()

        setContent {
            ImageGalleryTheme {
                val navController = rememberNavController()
                AppNavHost(navController)
                val viewModel: MainViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                if (uiState.hasToken) {
                    navController.navigate(Screen.Gallery.route)
                } else {
                    navController.navigate(Screen.Login.route)
                }
            }
        }
    }
}