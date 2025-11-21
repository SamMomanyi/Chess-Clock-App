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
            name = "• Rapid: 10|0",
            delay = null,
            countDown = 600_000L,
            increment = 0L,
        ),
        ClockFormat(
            id = 2,
            name = "• Blitz: 3|2",
            delay = null,
            countDown = 180_000L,
            increment = 2_000L,
        ),
        ClockFormat(
            id = 3,
            name = "• Rapid: 15|10",
            delay = null,
            countDown = 900_000L,
            increment = 10_000L,
        ),
        ClockFormat(
            id = 4,
            name = "• Classical: 30|0 ",
            delay = null,
            countDown = 1_800_000L,
            increment = 0L,
        ),

        ClockFormat(
            id = 5,
            name = "• Blitz: 5|5",
            delay = null,
            countDown = 300_000L,
            increment = 5_000L,
        ),
        ClockFormat(
            id = 6,
            name = "• Blitz: 5|0",
            delay = null,
            countDown = 300_000L,
            increment = 0L,
        )
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

