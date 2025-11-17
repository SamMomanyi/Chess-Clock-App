package com.example.chess_clock.AppUtils

import android.content.Context
import android.view.View
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.example.chess_clock.ui.theme.InactiveColorScheme

sealed interface TimerScreenCommand {
    //i think it would be logical to pass a different playerstate or something and not a countDown since it is somethings that is really used in the app including having to remember it and showing it in the UI
    data class PlayerClicked(
        val activatePlayer: ActivatePlayer,
        val playerState: PlayerState,
        val context: Context,
        val view: View
    ) : TimerScreenCommand
    //navigation commands
    data class OpenSettings(val navController: NavController): TimerScreenCommand
    data class OpenTimerSelection(val navController: NavController) : TimerScreenCommand // a data class since we will pass data and show the currently selected format
    object PauseClockClicked : TimerScreenCommand
    //setname commands
    data class SetNameClicked(val selectedPlayer : PlayerType) : TimerScreenCommand
    object HideNameDialog : TimerScreenCommand
    data class ConfirmSetName(val selectedPlayer : PlayerType,val name : String) : TimerScreenCommand
    //restart timer commands
    object RestartTimerClicked : TimerScreenCommand
    object ConfirmRestartClock : TimerScreenCommand
    object HideRestartTimerDialog : TimerScreenCommand

}

//a handy way to handle commands on the HomeScreen e.g click of a button
//from UI to viewModel
sealed interface TimerScreenEvent {

    data class ShowTimeExpiredSnackBar(val message: String) : TimerScreenEvent
    data class SetName(val playerName: String) : TimerScreenEvent //sets the name
    //navigation events
    data class NavigateToTimerSelection(val navController: NavController): TimerScreenEvent
    data class NavigateToSettings(val navController: NavController) : TimerScreenEvent
    data class ShowInvalidNameSnackBar (val message : String):  TimerScreenEvent

}

//a handy way to define and show current state  of the TimeScreen

data class TimeScreenState(
    val activePlayer: ActivatePlayer = ActivatePlayer.NONE,
    val isClockInitial: Boolean = true,  //whether any of the player's has activated the other clock and whether it's running
    val isGameOver: Boolean = false,
    //we can use a unified countDownTime to show on each players side according to the flatMapLatest
    val player_One_Name: String = "Player1",
    val player_Two_Name: String = "Player2",
    val countDownTime1: String = "00:00",
    val countDownTime2: String = "00:00",
    val player1State: PlayerState = PlayerState.INACTIVE,
    val player2State: PlayerState = PlayerState.INACTIVE,
    val colorScheme1: ColorScheme = InactiveColorScheme,
    val colorScheme2: ColorScheme = InactiveColorScheme,
    val player1Moves: Int = 0,
    val player2Moves: Int = 0,
    val microTime1: Int = 99,
    val microTime2: Int = 99,
    //UI booleans
    val showNameDialog : Boolean = false,
    val showRestartDialog : Boolean = false,
    val selectedPlayerForNameDialog :  PlayerType = PlayerType.ONE
)

data class ColorScheme(
    val borderColor: Color = Color.Gray,
    val contentColor: Color = Color.White,
    val backGroundColor: Color = Color.Gray,
    val activeIcon: ImageVector? = null,
)

//helper function to get the player state
fun PlayerType.toplayerState(state: TimeScreenState): PlayerState = when (this) {
    PlayerType.ONE -> state.player1State
    PlayerType.TWO -> state.player2State
}

//helper data class to return from combine
data class PlayerData(
    val name: String,
    val state: PlayerState,
    val mainTime: Long,
    val microTime: Int,
    val playerMoves: Int
)

//object routes for playerData
object routes {
    var screenA = "Timer_Screen"
    var screenB = "Timer_Selection"
    var screenC = "Settings_Screen"
}

enum class PlayerState {
    ACTIVE,
    INACTIVE,
    DEFEATED;
}

//both Active and Activate at the same time
enum class ActivatePlayer {
    ONE,
    TWO,
    NONE
}

enum class PlayerType {
    ONE,
    TWO
}
