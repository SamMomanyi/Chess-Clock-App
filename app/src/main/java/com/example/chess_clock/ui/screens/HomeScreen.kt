package com.example.chess_clock.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.chess_clock.AppUtils.ActivatePlayer
import com.example.chess_clock.AppUtils.HomeScreenCommand
import com.example.chess_clock.AppUtils.HomeScreenEvent
import com.example.chess_clock.AppUtils.PlayerState
import com.example.chess_clock.AppUtils.PlayerType
import com.example.chess_clock.AppUtils.TimeScreenState
import com.example.chess_clock.AppUtils.routes
import com.example.chess_clock.AppUtils.toplayerState
import com.example.chess_clock.ViewModel.HomeScreenViewModel
import com.example.chess_clock.model.daggerHilt.di.AppContext
import com.example.chess_clock.ui.ObserverAsEvents
import com.example.chess_clock.ui.editPlayerNameDialog
import com.example.chess_clock.ui.restartClockDialog


@Composable
fun HomeScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {

    //CurrentPage logic
    val state = viewModel.state.collectAsStateWithLifecycle()
    val event = viewModel.events
    val snackBarState = remember {
        SnackbarHostState()
    }

    ObserverAsEvents(
        event
    ) { event ->
        when (event) {
            is HomeScreenEvent.SetName -> {
                Unit
            }

            is HomeScreenEvent.ShowTimeExpiredSnackBar -> {
                // snackBarState.showSnackbar(message = event.message,duration = SnackbarDuration.Short,actionLabel = "Next")
                Toast.makeText(AppContext.getContext(), event.message, Toast.LENGTH_SHORT).show()
            }

            is HomeScreenEvent.NavigateToHomeSelection -> {
                event.navController.navigate(routes.screenB)
            }

            is HomeScreenEvent.ShowInvalidNameSnackBar -> {
                Toast.makeText(AppContext.getContext(), event.message, Toast.LENGTH_SHORT).show()
                //    snackBarState.showSnackbar(message = event.message,duration = SnackbarDuration.Short, actionLabel = "Invalid")
            }

            is HomeScreenEvent.NavigateToSettings -> {
                event.navController.navigate(routes.screenC)
            }
        }
    }
    //dialog listeners
    //this alltogether avoids calling the Alerts more than once at a time
    if (state.value.showNameDialog) {
        editPlayerNameDialog(
            onCommand = viewModel::HomeScreenCommandHandler,
            state = state.value,
            playerType = state.value.selectedPlayerForNameDialog,
        )
    }

    if (state.value.showRestartDialog) {
        restartClockDialog(
            onCommand = viewModel::HomeScreenCommandHandler,
        )
    }

    //beginning of UI
    Column(
        modifier = modifier
    ) {
        //player one activates player 2
        player(
            playerType = PlayerType.ONE,
            activePlayer = ActivatePlayer.TWO,
            state = state.value,
            onCommand = viewModel::HomeScreenCommandHandler,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.45F)
        )
        //where we keep the navigation
        //  Spacer(modifier = Modifier.weight(.10F))
        Navigation(
            navController = navController,
            modifier = Modifier.weight(0.10f),
            onCommand = viewModel::HomeScreenCommandHandler,
            state = state.value
        )
        //player two activates player 1
        player(
            playerType = PlayerType.TWO,
            activePlayer = ActivatePlayer.ONE,
            state = state.value,
            onCommand = viewModel::HomeScreenCommandHandler,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.45F)
        )
    }
}

@Composable
fun player(
    playerType: PlayerType,
    activePlayer: ActivatePlayer,
    state: TimeScreenState,
    onCommand: (HomeScreenCommand) -> Unit,
    modifier: Modifier
) {

    val view = LocalView.current
    val context = LocalContext.current
    //player is a card which calls playerContent
    //we will call each playerfunction twice  thus call each playerContent twice

    var playerState = playerType.toplayerState(state = state)


    Card(
        onClick = {
            Log.e("PlayerCLicked", "I was clicked 1")
            onCommand(
                HomeScreenCommand.PlayerClicked(
                    activatePlayer = activePlayer,
                    playerState = playerState,
                    context = context,
                    view = view,
                )
            )
            Log.e("check", "Clicked from the card")
        },
        modifier = modifier,
        enabled = (playerState == PlayerState.ACTIVE || state.isClockInitial),
        border = BorderStroke(10.dp, Color.White),
        shape = CardDefaults.elevatedShape
    ) {
        playerContent(
            playerType = playerType,
            playerState = playerState,
            state = state,
            cardEnabled = (playerState == PlayerState.ACTIVE || state.activePlayer == ActivatePlayer.NONE),
            onCommand = onCommand,
        )
    }
}

@Composable
fun playerContent(
    playerType: PlayerType,
    playerState: PlayerState,
    state: TimeScreenState,
    cardEnabled: Boolean,
    onCommand: (HomeScreenCommand) -> Unit,

    ) {

    val colorScheme = if (playerType == PlayerType.ONE) {
        state.colorScheme1
    } else {
        state.colorScheme2
    }
    val currentTime = if (playerType == PlayerType.ONE) {
        state.countDownTime1
    } else {
        state.countDownTime2
    }
    val currentMicroSecond = if (playerType == PlayerType.ONE) {
        state.microTime1
    } else {
        state.microTime2
    }
    val playerName = if (playerType == PlayerType.ONE) {
        state.player_One_Name
    } else {
        state.player_Two_Name
    }
    val playerMoves = if (playerType == PlayerType.ONE) {
        state.player1Moves
    } else {
        state.player2Moves
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp)
            .then(
                if (!cardEnabled && state.activePlayer == ActivatePlayer.NONE)
                    Modifier.clickable {
                        onCommand(HomeScreenCommand.RestartHomeClicked)
                    }
                else Modifier
            ),
        //remember we cannot also click the playerName button when the game is on
        //enabled = true, //(!cardEnabled && playerState == PlayerState.DEFEATED),
        shape = RectangleShape,
        border = BorderStroke(
            width = 10.dp,
            color = colorScheme.contentColor
        ),
        color = colorScheme.backGroundColor
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            //we have a row holding the delay the button and the icon?
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(80.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "delay time",
                    color = colorScheme.contentColor
                )
                Button(
                    onClick = {
                        onCommand(
                            HomeScreenCommand.SetNameClicked(
                                selectedPlayer = playerType
                            )
                        )
                        Log.e("SetName", "navigate to timerselection")
                    },
                    modifier = Modifier
                        .height(70.dp)
                        .width(120.dp),

                    elevation = ButtonDefaults.filledTonalButtonElevation(
                        defaultElevation = 20.dp,
                        disabledElevation = 20.dp
                    ),
                    shape = ButtonDefaults.filledTonalShape,
                    colors = ButtonColors(
                        containerColor = colorScheme.backGroundColor,
                        contentColor = colorScheme.contentColor,
                        disabledContainerColor = colorScheme.backGroundColor,
                        disabledContentColor = colorScheme.backGroundColor,
                    ),
                    enabled = (
                            state.activePlayer == ActivatePlayer.NONE
                            )
                ) {
                    Text(
                        text = playerName,
                        color = colorScheme.contentColor,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        fontSize = 20.sp
                    )
                }
                colorScheme.activeIcon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                    )
                }
            }


            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(
                        Alignment.CenterHorizontally
                    )
                    .padding(
                        start = 0.dp,
                        top = 20.dp,
                        bottom = 45.dp,
                        end = 0.dp
                    ),
                fontWeight = FontWeight.Bold,
                color = colorScheme.contentColor,
                textAlign = TextAlign.Center,
                fontSize = 90.sp,
                text = currentTime
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 0.dp,
                        bottom = 25.dp,
                        end = 0.dp
                    ),
                fontWeight = FontWeight.Bold,
                color = colorScheme.contentColor,
                textAlign = TextAlign.Center,
                fontSize = 70.sp,
                text = "$currentMicroSecond"
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                color = colorScheme.contentColor,
                textAlign = TextAlign.Right,
                fontSize = 35.sp,
                text = "Moves: $playerMoves"
            )
        }
    }
}

@Composable
fun Navigation(
    modifier: Modifier,
    state: TimeScreenState,
    navController: NavController,
    onCommand: (HomeScreenCommand) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = {
                onCommand(HomeScreenCommand.OpenHomeSelection(navController = navController))
                Log.e("Navigation", "navigate to timerselection")
                //  navController.navigate(routes.screenB)
            },
            modifier = Modifier
                .weight(0.3333333f)
                .fillMaxHeight(),
            enabled = (state.activePlayer == ActivatePlayer.NONE),
            colors = IconButtonDefaults.iconButtonColors(),
        ) {
            Icon(
                modifier = Modifier.size(70.dp),
                imageVector = Icons.Default.MoreTime,
                contentDescription = "TimerSelections",
            )
        }

        if(state.activePlayer == ActivatePlayer.NONE){
            IconButton(
                onClick = {
                    onCommand(HomeScreenCommand.OpenSettings(navController = navController))
                },
                modifier = Modifier
                    .weight(0.3333333f)
                    .fillMaxHeight(),
                enabled = (state.activePlayer == ActivatePlayer.NONE),
                colors = IconButtonDefaults.iconButtonColors(),
            ) {
                Icon(
                    modifier = Modifier.size(70.dp),
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                )
            }
        }
        else {
            IconButton(
                onClick = {
                    onCommand(HomeScreenCommand.PauseClockClicked)
                },
                modifier = Modifier
                    .weight(0.3333333f)
                    .fillMaxHeight(),
                enabled = (state.activePlayer != ActivatePlayer.NONE),
                colors = IconButtonDefaults.iconButtonColors(),
            ) {
                Icon(
                    modifier = Modifier.size(80.dp),
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause Clock",
                )
            }
        }


        IconButton(
            onClick = {
                onCommand(HomeScreenCommand.RestartHomeClicked)
            },
            modifier = Modifier
                .weight(0.333333f)
                .fillMaxHeight(),
            enabled = (state.activePlayer == ActivatePlayer.NONE),
            colors = IconButtonDefaults.iconButtonColors(),
        ) {
            Icon(
                modifier = Modifier.size(70.dp),
                imageVector = Icons.Default.RestartAlt,
                contentDescription = "Restart Timer"
            )
        }


    }
}

//Utility function to listen for events


