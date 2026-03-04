package com.example.chess_clock.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.chess_clock.ViewModel.ThemeViewModel
import com.example.chess_clock.model.daggerHilt.di.AppContext
import com.example.chess_clock.ui.ObserverAsEvents
import com.example.chess_clock.ui.editPlayerNameDialog
import com.example.chess_clock.ui.restartClockDialog
import com.example.chess_clock.ui.theme.AppTheme
import com.example.chess_clock.ui.theme.accentColor
import com.example.chess_clock.ui.theme.accentGlowColor

@Composable
fun HomeScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel(),
) {
    val state         = viewModel.state.collectAsStateWithLifecycle()
    val currentTheme  by themeViewModel.currentTheme.collectAsStateWithLifecycle()
    val event         = viewModel.events
    val snackBarState = remember { SnackbarHostState() }

    ObserverAsEvents(event) { event ->
        when (event) {
            is HomeScreenEvent.SetName -> Unit

            is HomeScreenEvent.ShowTimeExpiredSnackBar ->
                Toast.makeText(AppContext.getContext(), event.message, Toast.LENGTH_SHORT).show()

            is HomeScreenEvent.NavigateToHomeSelection ->
                event.navController.navigate(routes.screenB)

            is HomeScreenEvent.ShowInvalidNameSnackBar ->
                Toast.makeText(AppContext.getContext(), event.message, Toast.LENGTH_SHORT).show()

            is HomeScreenEvent.NavigateToSettings ->
                event.navController.navigate(routes.screenC)
        }
    }

    if (state.value.showNameDialog) {
        editPlayerNameDialog(
            onCommand  = viewModel::HomeScreenCommandHandler,
            state      = state.value,
            playerType = state.value.selectedPlayerForNameDialog,
        )
    }

    if (state.value.showRestartDialog) {
        restartClockDialog(onCommand = viewModel::HomeScreenCommandHandler)
    }

    // Themed background behind everything
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            player(
                playerType   = PlayerType.ONE,
                activePlayer = ActivatePlayer.TWO,
                state        = state.value,
                currentTheme = currentTheme,
                onCommand    = viewModel::HomeScreenCommandHandler,
                modifier     = Modifier.fillMaxWidth().weight(0.45F),
            )
            Navigation(
                navController = navController,
                modifier      = Modifier.weight(0.10f),
                onCommand     = viewModel::HomeScreenCommandHandler,
                state         = state.value,
            )
            player(
                playerType   = PlayerType.TWO,
                activePlayer = ActivatePlayer.ONE,
                state        = state.value,
                currentTheme = currentTheme,
                onCommand    = viewModel::HomeScreenCommandHandler,
                modifier     = Modifier.fillMaxWidth().weight(0.45F),
            )
        }
    }
}

@Composable
fun player(
    playerType: PlayerType,
    activePlayer: ActivatePlayer,
    state: TimeScreenState,
    currentTheme: AppTheme,
    onCommand: (HomeScreenCommand) -> Unit,
    modifier: Modifier,
) {
    val view        = LocalView.current
    val context     = LocalContext.current
    val playerState = playerType.toplayerState(state = state)
    val isActive    = playerState == PlayerState.ACTIVE

    // ── Pulsing glow animation ────────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue   = 0.3f,
        targetValue    = 1.0f,
        animationSpec  = infiniteRepeatable(
            animation  = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowAlpha"
    )

    val accent     = accentColor(currentTheme)
    val accentGlow = accentGlowColor(currentTheme)

    // Border: pulsing accent when active, subtle surface otherwise
    val borderColor = when {
        isActive -> accent.copy(alpha = glowAlpha)
        else     -> MaterialTheme.colorScheme.surfaceVariant
    }
    val borderWidth = if (isActive) 4.dp else 1.dp

    Card(
        onClick = {
            onCommand(
                HomeScreenCommand.PlayerClicked(
                    activatePlayer = activePlayer,
                    playerState    = playerState,
                    context        = context,
                    view           = view,
                )
            )
        },
        modifier  = modifier.then(
            // Extra glow drawn behind the card when active
            if (isActive) Modifier.drawBehind {
                drawRect(
                    color       = accentGlow.copy(alpha = glowAlpha * 0.4f),
                    style       = Stroke(width = 18.dp.toPx()),
                )
            } else Modifier
        ),
        enabled   = (playerState == PlayerState.ACTIVE || state.isClockInitial),
        border    = BorderStroke(borderWidth, borderColor),
        shape     = CardDefaults.elevatedShape,
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        playerContent(
            playerType  = playerType,
            playerState = playerState,
            state       = state,
            cardEnabled = (playerState == PlayerState.ACTIVE || state.activePlayer == ActivatePlayer.NONE),
            onCommand   = onCommand,
            accent      = accent,
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
    accent: Color,
) {
    val colorScheme = if (playerType == PlayerType.ONE) state.colorScheme1 else state.colorScheme2
    val currentTime = if (playerType == PlayerType.ONE) state.countDownTime1 else state.countDownTime2
    val currentMicroSecond = if (playerType == PlayerType.ONE) state.microTime1 else state.microTime2
    val playerName  = if (playerType == PlayerType.ONE) state.player_One_Name else state.player_Two_Name
    val playerMoves = if (playerType == PlayerType.ONE) state.player1Moves else state.player2Moves

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .then(
                if (!cardEnabled && state.activePlayer == ActivatePlayer.NONE)
                    Modifier.clickable { onCommand(HomeScreenCommand.RestartHomeClicked) }
                else Modifier
            ),
        shape  = RectangleShape,
        border = BorderStroke(
            width = 2.dp,
            color = colorScheme.contentColor.copy(alpha = 0.15f)
        ),
        color  = colorScheme.backGroundColor,
    ) {
        Column(
            verticalArrangement   = Arrangement.Top,
            horizontalAlignment   = Alignment.CenterHorizontally,
            modifier              = Modifier.fillMaxSize().padding(16.dp),
        ) {
            // Top row: delay label + name button + active icon
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier              = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text  = "delay",
                    color = colorScheme.contentColor.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                )

                Button(
                    onClick = {
                        onCommand(HomeScreenCommand.SetNameClicked(selectedPlayer = playerType))
                    },
                    modifier = Modifier.height(44.dp).width(140.dp),
                    elevation = ButtonDefaults.filledTonalButtonElevation(defaultElevation = 4.dp),
                    shape    = ButtonDefaults.filledTonalShape,
                    colors   = ButtonColors(
                        containerColor         = colorScheme.backGroundColor,
                        contentColor           = if (playerState == PlayerState.ACTIVE) accent else colorScheme.contentColor,
                        disabledContainerColor = colorScheme.backGroundColor,
                        disabledContentColor   = colorScheme.contentColor.copy(alpha = 0.4f),
                    ),
                    enabled = state.activePlayer == ActivatePlayer.NONE,
                ) {
                    Text(
                        text       = playerName,
                        fontWeight = FontWeight.Bold,
                        maxLines   = 1,
                        fontSize   = 18.sp,
                    )
                }

                colorScheme.activeIcon?.let { icon ->
                    Icon(
                        imageVector        = icon,
                        contentDescription = null,
                        tint               = accent,
                        modifier           = Modifier.size(24.dp),
                    )
                }
            }

            // Main countdown
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 30.dp),
                fontWeight  = FontWeight.Bold,
                color       = if (playerState == PlayerState.ACTIVE) accent else colorScheme.contentColor,
                textAlign   = TextAlign.Center,
                fontSize    = 88.sp,
                text        = currentTime,
            )

            // Centiseconds
            Text(
                modifier    = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                fontWeight  = FontWeight.Bold,
                color       = colorScheme.contentColor.copy(alpha = 0.7f),
                textAlign   = TextAlign.Center,
                fontSize    = 56.sp,
                text        = "$currentMicroSecond",
            )

            // Move counter
            Text(
                modifier    = Modifier.fillMaxWidth(),
                fontWeight  = FontWeight.SemiBold,
                color       = colorScheme.contentColor.copy(alpha = 0.6f),
                textAlign   = TextAlign.End,
                fontSize    = 28.sp,
                text        = "Moves: $playerMoves",
            )
        }
    }
}

@Composable
fun Navigation(
    modifier: Modifier,
    state: TimeScreenState,
    navController: NavController,
    onCommand: (HomeScreenCommand) -> Unit,
) {
    Row(
        modifier              = modifier
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        IconButton(
            onClick  = { onCommand(HomeScreenCommand.OpenHomeSelection(navController)) },
            modifier = Modifier.weight(0.333f).fillMaxHeight(),
            enabled  = (state.activePlayer == ActivatePlayer.NONE),
        ) {
            Icon(
                modifier           = Modifier.size(34.dp),
                imageVector        = Icons.Default.MoreTime,
                contentDescription = "Timer Selections",
                tint               = if (state.activePlayer == ActivatePlayer.NONE)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            )
        }

        if (state.activePlayer == ActivatePlayer.NONE) {
            IconButton(
                onClick  = { onCommand(HomeScreenCommand.OpenSettings(navController)) },
                modifier = Modifier.weight(0.333f).fillMaxHeight(),
            ) {
                Icon(
                    modifier           = Modifier.size(34.dp),
                    imageVector        = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint               = MaterialTheme.colorScheme.primary,
                )
            }
        } else {
            IconButton(
                onClick  = { onCommand(HomeScreenCommand.PauseClockClicked) },
                modifier = Modifier.weight(0.333f).fillMaxHeight(),
                enabled  = (state.activePlayer != ActivatePlayer.NONE),
            ) {
                Icon(
                    modifier           = Modifier.size(36.dp),
                    imageVector        = Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint               = MaterialTheme.colorScheme.primary,
                )
            }
        }

        IconButton(
            onClick  = { onCommand(HomeScreenCommand.RestartHomeClicked) },
            modifier = Modifier.weight(0.333f).fillMaxHeight(),
            enabled  = (state.activePlayer == ActivatePlayer.NONE),
        ) {
            Icon(
                modifier           = Modifier.size(34.dp),
                imageVector        = Icons.Default.RestartAlt,
                contentDescription = "Restart",
                tint               = if (state.activePlayer == ActivatePlayer.NONE)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            )
        }
    }
}