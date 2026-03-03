package com.example.chess_clock.ViewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chess_clock.AppUtils.AppUtil
import com.example.chess_clock.model.daggerHilt.MyRepositoryImplementation
import com.example.chess_clock.model.database.clocks.ClockFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── State ─────────────────────────────────────────────────────────────────────

data class EditTimerFormState(
    val isLoading: Boolean  = true,
    val name: String        = "",
    val minutes: String     = "5",
    val seconds: String     = "0",
    val incrementSec: String = "0",
    val delaySec: String    = "",
    val nameError: String?  = null,
    val timeError: String?  = null,
    val isSaving: Boolean   = false,
    val notFound: Boolean   = false,
)

// ── Events ────────────────────────────────────────────────────────────────────

sealed interface EditTimerEvent {
    object SavedAndNavigateBack : EditTimerEvent
    object DeletedAndNavigateBack : EditTimerEvent
    data class ShowError(val message: String) : EditTimerEvent
}

// ── Commands ──────────────────────────────────────────────────────────────────

sealed interface EditTimerCommand {
    data class NameChanged(val value: String) : EditTimerCommand
    data class MinutesChanged(val value: String) : EditTimerCommand
    data class SecondsChanged(val value: String) : EditTimerCommand
    data class IncrementChanged(val value: String) : EditTimerCommand
    data class DelayChanged(val value: String) : EditTimerCommand
    object Save : EditTimerCommand
    object Delete : EditTimerCommand
    object Cancel : EditTimerCommand
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

@HiltViewModel
class EditTimerViewModel @Inject constructor(
    private val database: MyRepositoryImplementation,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // The clock ID is pulled from the navigation argument "clockId"
    private val clockId: Int = checkNotNull(savedStateHandle["clockId"])

    private var originalClock: ClockFormat? = null

    private val _formState = MutableStateFlow(EditTimerFormState())
    val formState: StateFlow<EditTimerFormState> = _formState

    private val eventChannel = Channel<EditTimerEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        // Load the clock once from the database stream; find the one with our ID.
        viewModelScope.launch {
            database.getAllClockFormats().collect { clocks ->
                val clock = clocks.find { it.id == clockId }
                if (clock == null) {
                    _formState.value = _formState.value.copy(isLoading = false, notFound = true)
                    return@collect
                }
                // Only populate the form the very first time (don't reset mid-edit)
                if (originalClock == null) {
                    originalClock = clock
                    val (min, sec) = AppUtil.millisToMinSec(clock.countDown)
                    _formState.value = EditTimerFormState(
                        isLoading    = false,
                        name         = clock.name,
                        minutes      = min.toString(),
                        seconds      = sec.toString(),
                        incrementSec = ((clock.increment ?: 0L) / 1_000L).toString(),
                        delaySec     = clock.delay?.let { (it / 1_000L).toString() } ?: "",
                    )
                }
            }
        }
    }

    fun onCommand(command: EditTimerCommand) {
        when (command) {
            is EditTimerCommand.NameChanged ->
                _formState.value = _formState.value.copy(name = command.value, nameError = null)

            is EditTimerCommand.MinutesChanged ->
                _formState.value = _formState.value.copy(minutes = command.value, timeError = null)

            is EditTimerCommand.SecondsChanged ->
                _formState.value = _formState.value.copy(seconds = command.value, timeError = null)

            is EditTimerCommand.IncrementChanged ->
                _formState.value = _formState.value.copy(incrementSec = command.value)

            is EditTimerCommand.DelayChanged ->
                _formState.value = _formState.value.copy(delaySec = command.value)

            EditTimerCommand.Save   -> save()
            EditTimerCommand.Delete -> delete()
            EditTimerCommand.Cancel -> { /* handled in UI */ }
        }
    }

    private fun save() {
        val state = _formState.value
        val name  = state.name.trim()

        if (name.isBlank()) {
            _formState.value = state.copy(nameError = "Name cannot be empty")
            return
        }

        val minutes  = state.minutes.toIntOrNull() ?: 0
        val seconds  = state.seconds.toIntOrNull() ?: 0
        val totalMs  = AppUtil.minSecToMillis(minutes, seconds)
        if (totalMs <= 0L) {
            _formState.value = state.copy(timeError = "Time must be greater than zero")
            return
        }

        val increment = (state.incrementSec.toIntOrNull() ?: 0) * 1_000L
        val delay     = state.delaySec.toLongOrNull()?.let { it * 1_000L }

        _formState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            database.updateTimeFormat(
                ClockFormat(
                    id        = clockId,
                    name      = name,
                    countDown = totalMs,
                    increment = increment,
                    delay     = delay,
                )
            )
            eventChannel.trySend(EditTimerEvent.SavedAndNavigateBack)
        }
    }

    private fun delete() {
        val clock = originalClock ?: return
        viewModelScope.launch {
            database.deleteTimeFormat(clock)
            eventChannel.trySend(EditTimerEvent.DeletedAndNavigateBack)
        }
    }
}