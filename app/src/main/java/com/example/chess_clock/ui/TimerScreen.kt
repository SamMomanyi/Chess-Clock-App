package com.example.chess_clock.ui

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
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.example.chess_clock.AppUtils.ActivatePlayer
import com.example.chess_clock.AppUtils.HomeScreenCommand
import com.example.chess_clock.AppUtils.HomeScreenEvent
import com.example.chess_clock.AppUtils.PlayerState
import com.example.chess_clock.AppUtils.PlayerType
import com.example.chess_clock.AppUtils.TimeScreenState
import com.example.chess_clock.AppUtils.routes
import com.example.chess_clock.AppUtils.toplayerState
import com.example.chess_clock.ViewModel.clockViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.reflect.KSuspendFunction1


@Composable
fun TimerScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: clockViewModel = hiltViewModel()
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
            is HomeScreenEvent.ShowRestartTimerDialog -> {

            }

            HomeScreenEvent.HideNameDialog -> {
                Unit
            }

            is HomeScreenEvent.SetName -> {
                Unit
            }

            is HomeScreenEvent.ShowTimeExpiredSnackBar -> {
                snackBarState.showSnackbar(event.messagi)
            }
            //we call the dialog
            HomeScreenEvent.ShowNameDialog -> {
                Unit
            }
        }
    }
    Column(
        modifier = modifier
    ) {
        //player one activates player 2
        player(
            playerType = PlayerType.ONE,
            activePlayer = ActivatePlayer.TWO,
            state = state.value,
            onCommand = viewModel::CommandHandler,
            onEvent = viewModel::EventHandler,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.45F)
        )
        //where we keep the navigation
        //  Spacer(modifier = Modifier.weight(.10F))
        Navigation(navController = navController, modifier = Modifier.weight(0.10f))
        //player two activates player 1
        player(
            playerType = PlayerType.TWO,
            activePlayer = ActivatePlayer.ONE,
            state = state.value,
            onCommand = viewModel::CommandHandler,
            onEvent = viewModel::EventHandler,
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
    onEvent: KSuspendFunction1<HomeScreenEvent, Unit>,
    modifier: Modifier
) {

    var MoveCounter = remember {
        mutableStateOf(0)
    }
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
            MoveCounter.value += 1
            Log.e("check", "Clicked from the card")
        },
        modifier = modifier,
        enabled = (playerState == PlayerState.ACTIVE || state.activePlayer == ActivatePlayer.NONE),
        border = BorderStroke(10.dp, Color.White),
        shape = CardDefaults.elevatedShape
    ) {
        //if button is enabled we use the Card's button
        // else if inactive we cannot click
        //if clicked when one is defeated we call the restart object
        playerContent(
            playerType = playerType,
            playerState = playerState,
            state = state,
            cardEnabled = (playerState == PlayerState.ACTIVE || state.activePlayer == ActivatePlayer.NONE),
            onCommand = onCommand,
            moveCounter = MoveCounter,
            onEvent = onEvent
        )
    }
}

@Composable
fun playerContent(
    playerType: PlayerType,
    playerState: PlayerState,
    state: TimeScreenState,
    cardEnabled: Boolean,
    moveCounter: MutableState<Int>,
    onCommand: (HomeScreenCommand) -> Unit,
    onEvent: KSuspendFunction1<HomeScreenEvent, Unit>,

    ) {

    var colorScheme = if (playerType == PlayerType.ONE) {
        state.colorScheme1
    } else {
        state.colorScheme2
    }
    var currentTime = if (playerType == PlayerType.ONE) {
        state.countDownTime1
    } else {
        state.countDownTime2
    }
    var currentMicroSecond = if (playerType == PlayerType.ONE) {
        state.microTime1
    } else {
        state.microTime2
    }
    var playerName = if (playerType == PlayerType.ONE) {
        state.player_One_Name
    } else {
        state.player_Two_Name
    }
    var playerMoves = if (playerType == PlayerType.ONE) {
        state.player1Moves
    } else {
        state.player2Moves
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp)
            .then(
                if (!cardEnabled && playerState == PlayerState.DEFEATED)
                    Modifier.clickable {
                        Log.e("check", "Clicked from the surface")
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
                        HomeScreenEvent.ShowNameDialog
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
                        color = colorScheme.contentColor
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
fun editPlayerName(
    onEvent: (HomeScreenEvent) -> Unit,
    activatePlayer: ActivatePlayer
) {

    val context = LocalContext.current
    var playerName = remember {
        mutableStateOf("")
    }

    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(),
        onDismissRequest = {

        },
        title = {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                text = "enter name"
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextField(
                    value = playerName.value,
                    //means we are changing the value of the playerButton name
                    onValueChange = {
                        if (it.isBlank()) {
                            Toast.makeText(
                                context,
                                "Enter valid name",
                                Toast.LENGTH_SHORT
                            )
                        } else {
                            onEvent(HomeScreenEvent.SetName(it))
                        }
                    },
                    placeholder = {
                        Text(
                            text = "playername",
                            maxLines = 1
                        )
                    }
                )
            }
        },
        confirmButton = {

        },
        dismissButton = {

        }
    )
}

@Composable
fun Navigation(modifier: Modifier, navController: NavController) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = {
                navController.navigate(routes.screenB)
            },
            modifier = Modifier
                .weight(0.3333333f)
                .fillMaxHeight(),
            enabled = (true),
            colors = IconButtonDefaults.iconButtonColors(),
        ) {
            Icon(
                modifier = Modifier.size(70.dp),
                imageVector = Icons.Default.MoreTime,
                contentDescription = "TimerSelections",
            )
        }

        IconButton(
            onClick = {
                navController.navigate(routes.screenC)
            },
            modifier = Modifier
                .weight(0.3333333f)
                .fillMaxHeight(),
            enabled = (true),
            colors = IconButtonDefaults.iconButtonColors(),
        ) {
            Icon(
                modifier = Modifier.size(70.dp),
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
            )
        }

        IconButton(
            onClick = {

            },
            modifier = Modifier
                .weight(0.333333f)
                .fillMaxHeight(),
            enabled = (true),
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
@Composable
fun <T> ObserverAsEvents(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: suspend (T) -> Unit
) {
    val lifeCycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifeCycleOwner, key1, key2) {
        lifeCycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}

