package com.example.chess_clock.AppUtils

import androidx.navigation.NavController
import com.example.chess_clock.model.database.clocks.ClockFormat

// ── Commands (UI → ViewModel) ────────────────────────────────────────────────

sealed interface TimerSelectionCommands {

    /** User tapped a clock card to select it */
    data class CardClicked(val clock: ClockFormat) : TimerSelectionCommands

    /** User confirmed their selection and wants to start */
    data class StartButtonClicked(val navController: NavController) : TimerSelectionCommands

    /** User wants to edit an existing clock */
    data class EditTimerClicked(
        val clock: ClockFormat,
        val navController: NavController
    ) : TimerSelectionCommands

    /** User wants to add a brand-new clock */
    data class AddTimeClicked(val navController: NavController) : TimerSelectionCommands

    /** User wants to delete a clock */
    data class DeleteTimerClicked(val clock: ClockFormat) : TimerSelectionCommands
}

// ── Events (ViewModel → UI) ──────────────────────────────────────────────────

sealed interface TimerSelectionEvents {

    /** Navigate back to the home / timer screen */
    data class NavigateToHomeScreen(val navController: NavController) : TimerSelectionEvents

    /** Navigate to the edit screen, carrying the clock's ID */
    data class NavigateToEditTimerScreen(
        val navController: NavController,
        val clockId: Int
    ) : TimerSelectionEvents

    /** Navigate to the add-new-clock screen */
    data class NavigateToAddTimerScreen(val navController: NavController) : TimerSelectionEvents
}

// ── State ────────────────────────────────────────────────────────────────────

data class TimerSelectionState(
    val clocks: List<ClockFormat> = AppUtil.predefinedClockFormats,
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedClock: ClockFormat? = AppUtil.predefinedClockFormats.firstOrNull()
)