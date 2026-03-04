package com.example.chess_clock.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material.icons.filled.NearbyError
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.chess_clock.AppUtils.AppUtil
import com.example.chess_clock.AppUtils.TimerSelectionCommands
import com.example.chess_clock.AppUtils.TimerSelectionEvents
import com.example.chess_clock.AppUtils.routes
import com.example.chess_clock.R
import com.example.chess_clock.ViewModel.TimerSelectionViewModel
import com.example.chess_clock.model.database.clocks.ClockFormat
import com.example.chess_clock.model.database.clocks.DatabaseResponse
import com.example.chess_clock.ui.ObserverAsEvents

private val Gold          = Color(0xFFFFD700)
private val DarkBg        = Color(0xFF1A1A2E)
private val CardBg        = Color(0xFF16213E)
private val SelectedCardBg = Color(0xFF0F3460)

@Composable
fun TimerSelection(
    modifier: Modifier,
    navController: NavController,
    viewModel: TimerSelectionViewModel = hiltViewModel()
) {
    val state    by viewModel.state.collectAsStateWithLifecycle()
    val dbState  by viewModel.dbResponse.collectAsState()
    val onCommand = viewModel::TimerSelectionCommandHandler

    ObserverAsEvents(viewModel.events) { event ->
        when (event) {
            is TimerSelectionEvents.NavigateToEditTimerScreen ->
                event.navController.navigate(routes.editTimer(event.clockId))
            is TimerSelectionEvents.NavigateToAddTimerScreen ->
                event.navController.navigate(routes.screenE)
            is TimerSelectionEvents.NavigateToHomeScreen ->
                event.navController.popBackStack()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Hero image ────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Image(
                    painter            = painterResource(R.drawable.chess_landscape_image),
                    contentDescription = "Chess landscape",
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize()
                )
                // Fade the image into the dark background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, DarkBg),
                                startY = 120f,
                            )
                        )
                )
                Text(
                    text       = "Select Time Control",
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 22.sp,
                    modifier   = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 12.dp)
                )
            }

            // ── Clock list ────────────────────────────────────────────────────
            LazyColumn(
                modifier            = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding      = PaddingValues(
                    start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp
                )
            ) {
                when (val result = dbState) {
                    is DatabaseResponse.Success -> {
                        if (result.data.isEmpty()) {
                            item {
                                Column(
                                    modifier            = Modifier.fillMaxWidth().padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("No clocks found", color = Color.White, fontSize = 16.sp)
                                    Spacer(Modifier.height(4.dp))
                                    Text("Tap + to add one", color = Color.Gray, fontSize = 14.sp)
                                }
                            }
                        } else {
                            items(items = result.data, key = { it.id }) { clock ->
                                SwipeToDeleteTimerCard(
                                    clock         = clock,
                                    isSelected    = state.selectedClock?.id == clock.id,
                                    navController = navController,
                                    onCommand     = { onCommand(it) },
                                )
                            }
                        }
                    }
                    DatabaseResponse.isLoading ->
                        item {
                            Box(
                                modifier         = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator(color = Gold) }
                        }
                    is DatabaseResponse.Failed ->
                        item { FailedScreen(result.message ?: "Failed to load clocks") }
                }
            }

            // ── Bottom action bar ─────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D0D1A))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FloatingActionButton(
                    onClick        = { onCommand(TimerSelectionCommands.StartButtonClicked(navController)) },
                    modifier       = Modifier.weight(1f).height(52.dp),
                    shape          = RoundedCornerShape(14.dp),
                    containerColor = Gold,
                    contentColor   = Color.Black,
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(22.dp))
                        Text("Start", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }

                FloatingActionButton(
                    onClick = {
                        state.selectedClock?.let {
                            onCommand(TimerSelectionCommands.EditTimerClicked(it, navController))
                        }
                    },
                    modifier       = Modifier.size(52.dp),
                    shape          = RoundedCornerShape(14.dp),
                    containerColor = CardBg,
                    contentColor   = Color.White,
                ) {
                    Icon(Icons.Default.ModeEdit, contentDescription = "Edit", modifier = Modifier.size(22.dp))
                }

                FloatingActionButton(
                    onClick        = { onCommand(TimerSelectionCommands.AddTimeClicked(navController)) },
                    modifier       = Modifier.size(52.dp),
                    shape          = RoundedCornerShape(14.dp),
                    containerColor = CardBg,
                    contentColor   = Gold,
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}

// ── Swipe-to-delete wrapper ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteTimerCard(
    clock: ClockFormat,
    isSelected: Boolean,
    navController: NavController,
    onCommand: (TimerSelectionCommands) -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onCommand(TimerSelectionCommands.DeleteTimerClicked(clock))
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state                       = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent           = {
            Box(
                modifier         = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFB00020)),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector        = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint               = Color.White,
                    modifier           = Modifier.padding(end = 24.dp).size(26.dp),
                )
            }
        },
    ) {
        TimerCard(
            clock         = clock,
            isSelected    = isSelected,
            navController = navController,
            onCommand     = onCommand,
        )
    }
}

// ── Individual timer card ─────────────────────────────────────────────────────

@Composable
fun TimerCard(
    clock: ClockFormat,
    isSelected: Boolean,
    navController: NavController,
    onCommand: (TimerSelectionCommands) -> Unit,
) {
    val (minutes, seconds) = AppUtil.millisToMinSec(clock.countDown)
    val incrementSec       = (clock.increment ?: 0L) / 1_000L
    val timeLabel          = if (seconds == 0) "${minutes}m" else "${minutes}m ${seconds}s"
    val incrementLabel     = if (incrementSec > 0L) "+${incrementSec}s" else "No increment"

    Card(
        onClick   = { onCommand(TimerSelectionCommands.CardClicked(clock)) },
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        border    = if (isSelected) BorderStroke(2.dp, Gold) else null,
        colors    = CardDefaults.cardColors(
            containerColor = if (isSelected) SelectedCardBg else CardBg,
            contentColor   = Color.White,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        ),
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text       = clock.name,
                    fontWeight = FontWeight.Light,
                    fontSize   = 12.sp,
                    color      = if (isSelected) Gold.copy(alpha = 0.85f) else Color.Gray,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Text(
                        text       = timeLabel,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 22.sp,
                        color      = if (isSelected) Gold else Color.White,
                    )
                    Text(
                        text     = incrementLabel,
                        fontSize = 13.sp,
                        color    = Color.Gray,
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .background(Gold.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Selected", fontSize = 11.sp, color = Gold, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.width(8.dp))
                }
                IconButton(
                    onClick  = { onCommand(TimerSelectionCommands.EditTimerClicked(clock, navController)) },
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        imageVector        = Icons.Default.ModeEdit,
                        contentDescription = "Edit ${clock.name}",
                        tint               = Color.Gray,
                        modifier           = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun FailedScreen(message: String) {
    ExtendedFloatingActionButton(
        onClick        = {},
        icon           = { Icon(Icons.Default.NearbyError, contentDescription = "Error") },
        text           = { Text(message) },
        containerColor = Color.Cyan,
        contentColor   = Color.White,
    )
}