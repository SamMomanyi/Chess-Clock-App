package com.example.chess_clock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chess_clock.AppUtils.routes
import com.example.chess_clock.ViewModel.clockViewModel
import com.example.chess_clock.ui.SettingsScreen
import com.example.chess_clock.ui.TimerScreen
import com.example.chess_clock.ui.TimerSelection
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
fun MainScreen(modifier: Modifier ) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = routes.screenA,
        builder = {
            composable(routes.screenA){
                TimerScreen(modifier = modifier,navController = navController)
            }
            composable(routes.screenB){
                TimerSelection(modifier = modifier,navController = navController)
            }
            composable(routes.screenC){
                SettingsScreen(navController = navController)
            }

        }
    )

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Chess_clockTheme {

    }
}