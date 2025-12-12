package com.example.chess_clock.AppUtils

import androidx.navigation.NavController
import com.example.chess_clock.model.database.clocks.ClockFormat

sealed interface TimerSelectionCommands{

    object StartButtonClicked : TimerSelectionCommands
    //whenever a card is clicked we then send the data and update the selectedTimeFormat to use
    data class  CardClicked (val clock : ClockFormat): TimerSelectionCommands
    object EditTimerClicked : TimerSelectionCommands
    object AddTimeClicked : TimerSelectionCommands
}

sealed interface TimerSelectionEvents{
    object CardSelected : TimerSelectionEvents
    data class NavigateToHomeScreen(val navController: NavController) : TimerSelectionEvents
    data class NavigateToEditTimerScreen(val navController : NavController) : TimerSelectionEvents
    data class NavigateToAddTimerScreen(val navController: NavController) : TimerSelectionEvents
}

data class TimerSelectionState(
    val clocks : List<ClockFormat> = AppUtil.predefinedClockFormats,
    val isLoading : Boolean = true,
    val error : String? = null,
    val selectedClock : ClockFormat? =  AppUtil.predefinedClockFormats.get(index = 1)
)