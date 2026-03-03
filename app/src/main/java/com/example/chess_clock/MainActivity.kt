package com.example.chess_clock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chess_clock.AppUtils.routes
import com.example.chess_clock.ui.SettingsScreen
import com.example.chess_clock.ui.screens.AddTimerScreen
import com.example.chess_clock.ui.screens.EditTimerScreen
import com.example.chess_clock.ui.screens.HomeScreen
import com.example.chess_clock.ui.screens.TimerSelection
import com.example.chess_clock.ui.theme.Chess_clockTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Chess_clockTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController    = navController,
        startDestination = routes.screenA,
    ) {
        composable(routes.screenA) {
            HomeScreen(modifier = modifier, navController = navController)
        }

        composable(routes.screenB) {
            TimerSelection(modifier = modifier, navController = navController)
        }

        composable(routes.screenC) {
            SettingsScreen(navController = navController)
        }

        // EditTimerScreen receives the clock ID as a nav argument.
        // routes.screenD is "EditTimerScreen/{clockId}"
        // Navigate to it using routes.editTimer(id), e.g. "EditTimerScreen/3"
        composable(
            route     = routes.screenD,
            arguments = listOf(
                navArgument("clockId") { type = NavType.IntType }
            )
        ) {
            EditTimerScreen(navController = navController)
            // EditTimerViewModel reads clockId from SavedStateHandle automatically
        }

        composable(routes.screenE) {
            AddTimerScreen(navController = navController)
        }
    }
}