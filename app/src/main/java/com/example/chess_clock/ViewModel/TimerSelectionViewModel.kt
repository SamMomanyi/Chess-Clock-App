package com.example.chess_clock.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chess_clock.AppUtils.TimerSelectionCommands
import com.example.chess_clock.AppUtils.TimerSelectionEvents
import com.example.chess_clock.AppUtils.TimerSelectionState
import com.example.chess_clock.AppUtils.routes
import com.example.chess_clock.model.SelectedClockRepository
import com.example.chess_clock.model.daggerHilt.MyRepositoryImplementation
import com.example.chess_clock.model.database.clocks.ClockFormat
import com.example.chess_clock.model.database.clocks.DatabaseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerSelectionViewModel @Inject constructor(
    private val database: MyRepositoryImplementation,
    // Shared repository — writing here is immediately visible in HomeScreenViewModel
    private val selectedClockRepo: SelectedClockRepository,
) : ViewModel() {

    // ── Database stream ───────────────────────────────────────────────────────

    private val _dbResponse =
        MutableStateFlow<DatabaseResponse<List<ClockFormat>>>(DatabaseResponse.isLoading)
    val dbResponse = _dbResponse   // exposed for the legacy direct collector in the UI

    init {
        viewModelScope.launch {
            database.getAllClockFormats()
                .onStart  { _dbResponse.value = DatabaseResponse.isLoading }
                .catch    { ex -> _dbResponse.value = DatabaseResponse.Failed(ex.message) }
                .collect  { data -> _dbResponse.value = DatabaseResponse.Success(data) }
        }
    }

    // ── Selected clock ────────────────────────────────────────────────────────

    // Mirrors whatever is in the shared repo so the UI can highlight the right card.
    private val _selectedClock = MutableStateFlow(selectedClockRepo.selectedClock.value)

    // ── Events ────────────────────────────────────────────────────────────────

    private val eventChannel = Channel<TimerSelectionEvents>()
    val events = eventChannel.receiveAsFlow()

    // ── State ─────────────────────────────────────────────────────────────────

    val state = combine(_dbResponse, _selectedClock) { db, selected ->
        when (db) {
            is DatabaseResponse.Failed  ->
                TimerSelectionState(isLoading = false, error = db.message ?: "Failed to load clocks")

            is DatabaseResponse.Success ->
                TimerSelectionState(clocks = db.data, isLoading = false, selectedClock = selected)

            DatabaseResponse.isLoading  ->
                TimerSelectionState(isLoading = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), TimerSelectionState())

    // ── Command handler ───────────────────────────────────────────────────────

    fun TimerSelectionCommandHandler(command: TimerSelectionCommands) {
        when (command) {

            // Select a card (highlight it, store in shared repo so HomeScreen updates)
            is TimerSelectionCommands.CardClicked -> {
                _selectedClock.value = command.clock
                selectedClockRepo.selectClock(command.clock)
            }

            // Confirm selection → navigate back to the home screen
            is TimerSelectionCommands.StartButtonClicked -> {
                selectedClockRepo.selectClock(_selectedClock.value)
                command.navController.popBackStack()
            }

            // Navigate to the edit screen for a specific clock
            is TimerSelectionCommands.EditTimerClicked -> {
                eventChannel.trySend(
                    TimerSelectionEvents.NavigateToEditTimerScreen(
                        navController = command.navController,
                        clockId       = command.clock.id,
                    )
                )
            }

            // Navigate to the add-new-clock screen
            is TimerSelectionCommands.AddTimeClicked -> {
                eventChannel.trySend(
                    TimerSelectionEvents.NavigateToAddTimerScreen(command.navController)
                )
            }

            // Delete a clock from the database
            is TimerSelectionCommands.DeleteTimerClicked -> {
                viewModelScope.launch {
                    database.deleteTimeFormat(command.clock)
                    // If the deleted clock was selected, fall back to the first available
                    if (_selectedClock.value.id == command.clock.id) {
                        val fallback = (state.value.clocks - command.clock).firstOrNull()
                        if (fallback != null) {
                            _selectedClock.value = fallback
                            selectedClockRepo.selectClock(fallback)
                        }
                    }
                }
            }
        }
    }
}