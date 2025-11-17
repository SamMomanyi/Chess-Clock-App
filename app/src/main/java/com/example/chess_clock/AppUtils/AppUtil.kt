package com.example.chess_clock.AppUtils

import android.content.Context
import android.view.View
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.example.chess_clock.model.database.clocks.ClockFormat
import com.example.chess_clock.ui.theme.InactiveColorScheme

object AppUtil {
    val predefinedClockFormats: List<ClockFormat> = mutableListOf(
        ClockFormat(
            id = 1,
            delay = null,
            countDown = 10_000L,
            increment = 2000L,
            microSecondCountDown = 100L
        ),
        ClockFormat(
            id = 2,
            delay = null,
            countDown = 800_000L,
            increment = 2000L,
            microSecondCountDown = 100L
        ),
    )

    //helper function to convert time in mm:ss format

    fun formatTime(time: Long): String {
        val minutes = time / 60000
        val seconds = (time % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }
}


//a handy way to handle commands on the HomeScreen e.g click of a button
//from UI to viewModel

