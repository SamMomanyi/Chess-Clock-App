package com.example.chess_clock.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material.icons.filled.NearbyError
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
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
import com.example.chess_clock.model.daggerHilt.di.AppContext
import com.example.chess_clock.ui.ObserverAsEvents
import com.example.chess_clock.ui.theme.ActiveColorScheme
import com.example.chess_clock.ui.theme.InactiveColorScheme

@Composable
fun TimerSelection(
    modifier: Modifier,
    navController: NavController,
    viewModel: TimerSelectionViewModel = hiltViewModel()
) {
    val state    by viewModel.state.collectAsStateWithLifecycle()
    val dbState  by viewModel.dbResponse.collectAsState()
    val onCommand = viewModel::TimerSelectionCommandHandler

    // Observe navigation events
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

    Column(modifier = modifier) {

        // Hero image
        Image(
            painter            = painterResource(R.drawable.chess_landscape_image),
            contentDescription = "Chess landscape",
            modifier           = Modifier
                .weight(0.15f)
                .fillMaxWidth()
        )

        // Clock list
        LazyColumn(modifier = Modifier.weight(0.75f)) {
            when (val result = dbState) {
                is DatabaseResponse.Success -> {
                    if (result.data.isEmpty()) {
                        item {
                            Column(
                                modifier               = Modifier.fillMaxWidth().padding(20.dp),
                                horizontalAlignment    = Alignment.CenterHorizontally
                            ) {
                                Text("No clocks found", color = Color.White)
                                Text("Tap + to add one",  color = Color.White)
                            }
                        }
                    } else {
                        items(
                            items = result.data,
                            key   = { it.id }
                        ) { clock ->
                            SwipeToDeleteTimerCard(
                                clock      = clock,
                                isSelected = state.selectedClock?.id == clock.id,
                                onCommand  = { onCommand(it) },
                                navController = navController,
                            )
                        }
                    }
                }

                DatabaseResponse.isLoading ->
                    item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }

                is DatabaseResponse.Failed ->
                    item { FailedScreen(result.message ?: "Failed to load clocks") }
            }
        }

        // Bottom action row
        Row(
            modifier            = Modifier
                .weight(0.10f)
                .padding(horizontal = 12.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            // Start
            FloatingActionButton(
                onClick        = { onCommand(TimerSelectionCommands.StartButtonClicked(navController)) },
                modifier       = Modifier.weight(0.40f),
                contentColor   = ActiveColorScheme.contentColor,
                containerColor = ActiveColorScheme.backGroundColor,
            ) {
                Text(
                    text       = "Start",
                    fontStyle  = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 20.sp,
                )
            }

            Spacer(modifier = Modifier.weight(0.30f))

            // Edit (grayed out when nothing is selected)
            FloatingActionButton(
                onClick        = {
                    val selected = state.selectedClock
                    if (selected != null) {
                        onCommand(TimerSelectionCommands.EditTimerClicked(selected, navController))
                    }
                },
                modifier       = Modifier.weight(0.10f).size(60.dp),
                contentColor   = InactiveColorScheme.contentColor,
                containerColor = InactiveColorScheme.backGroundColor,
            ) {
                Icon(Icons.Default.ModeEdit, contentDescription = "Edit selected clock")
            }

            Spacer(modifier = Modifier.weight(0.05f))

            // FIX: was incorrectly calling EditTimerClicked; now correctly calls AddTimeClicked
            FloatingActionButton(
                onClick        = { onCommand(TimerSelectionCommands.AddTimeClicked(navController)) },
                modifier       = Modifier.weight(0.10f).size(60.dp),
                contentColor   = InactiveColorScheme.contentColor,
                containerColor = InactiveColorScheme.backGroundColor,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add new clock")
            }
        }
    }
}

// ── Timer card with swipe-to-delete ──────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteTimerCard(
    clock: ClockFormat,
    isSelected: Boolean,
    navController: NavController,
    onCommand: (TimerSelectionCommands) -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onCommand(TimerSelectionCommands.DeleteTimerClicked(clock))
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state            = dismissState,
        backgroundContent = {
            // Red delete background shown while swiping
            Box(
                modifier            = Modifier
                    .fillMaxSize()
                    .padding(start = 10.dp, top = 3.dp, end = 10.dp)
                    .background(Color(0xFFB00020), CardDefaults.elevatedShape),
                contentAlignment    = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector        = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint               = Color.White,
                    modifier           = Modifier.padding(end = 20.dp),
                )
            }
        },
        enableDismissFromStartToEnd = false,
    ) {
        TimerCard(
            clock        = clock,
            isSelected   = isSelected,
            navController = navController,
            onCommand    = onCommand,
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
    val incrementLabel     = if (incrementSec > 0) "+${incrementSec}s" else "No increment"

    Card(
        // FIX: Card is now clickable — CardClicked command fires on tap
        onClick   = { onCommand(TimerSelectionCommands.CardClicked(clock)) },
        modifier  = Modifier
            .padding(start = 10.dp, top = 4.dp, end = 10.dp)
            .fillMaxWidth()
            .height(70.dp),
        shape     = CardDefaults.elevatedShape,
        border    = if (isSelected) BorderStroke(2.dp, ActiveColorScheme.backGroundColor) else null,
        colors    = CardDefaults.cardColors(
            contentColor   = if (isSelected)
                contentColorFor(MaterialTheme.colorScheme.primaryContainer)
            else
                contentColorFor(MaterialTheme.colorScheme.surfaceVariant),
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp, pressedElevation = 12.dp),
    ) {
        Row(
            modifier              = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text  = clock.name,
                    style = TextStyle(fontWeight = FontWeight.Light, fontSize = 13.sp, color = Color.Gray),
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text  = "${minutes}m ${seconds}s",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                    )
                    Text(
                        text  = incrementLabel,
                        style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, color = Color.Gray),
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                }
            }

            // Quick edit button on the card
            IconButton(
                onClick = { onCommand(TimerSelectionCommands.EditTimerClicked(clock, navController)) }
            ) {
                Icon(
                    imageVector        = Icons.Default.ModeEdit,
                    contentDescription = "Edit ${clock.name}",
                    modifier           = Modifier.size(20.dp),
                    tint               = Color.Gray,
                )
            }
        }
    }
}

@Composable
fun FailedScreen(message: String) {
    ExtendedFloatingActionButton(
        onClick        = { },
        icon           = { Icon(Icons.Default.NearbyError, contentDescription = "Error") },
        text           = { Text(message) },
        containerColor = Color.Cyan,
        contentColor   = Color.White,
    )
}