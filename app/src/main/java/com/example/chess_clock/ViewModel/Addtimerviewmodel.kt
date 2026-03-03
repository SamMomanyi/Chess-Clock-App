package com.example.chess_clock.ViewModel

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

data class AddTimerFormState(
    val name: String        = "",
    val minutes: String     = "5",
    val seconds: String     = "0",
    val incrementSec: String = "0",
    val delaySec: String    = "",      // blank = no delay
    val nameError: String?  = null,
    val timeError: String?  = null,
    val isSaving: Boolean   = false,
)

// ── Events ────────────────────────────────────────────────────────────────────

sealed interface AddTimerEvent {
    object SavedAndNavigateBack : AddTimerEvent
    data class ShowError(val message: String) : AddTimerEvent
}

// ── Commands ──────────────────────────────────────────────────────────────────

sealed interface AddTimerCommand {
    data class NameChanged(val value: String) : AddTimerCommand
    data class MinutesChanged(val value: String) : AddTimerCommand
    data class SecondsChanged(val value: String) : AddTimerCommand
    data class IncrementChanged(val value: String) : AddTimerCommand
    data class DelayChanged(val value: String) : AddTimerCommand
    object Save : AddTimerCommand
    object Cancel : AddTimerCommand
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

@HiltViewModel
class AddTimerViewModel @Inject constructor(
    private val database: MyRepositoryImplementation,
) : ViewModel() {

    private val _formState = MutableStateFlow(AddTimerFormState())
    val formState: StateFlow<AddTimerFormState> = _formState

    private val eventChannel = Channel<AddTimerEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onCommand(command: AddTimerCommand) {
        when (command) {
            is AddTimerCommand.NameChanged ->
                _formState.value = _formState.value.copy(name = command.value, nameError = null)

            is AddTimerCommand.MinutesChanged ->
                _formState.value = _formState.value.copy(minutes = command.value, timeError = null)

            is AddTimerCommand.SecondsChanged ->
                _formState.value = _formState.value.copy(seconds = command.value, timeError = null)

            is AddTimerCommand.IncrementChanged ->
                _formState.value = _formState.value.copy(incrementSec = command.value)

            is AddTimerCommand.DelayChanged ->
                _formState.value = _formState.value.copy(delaySec = command.value)

            AddTimerCommand.Save -> save()
            AddTimerCommand.Cancel -> { /* handled by UI via navController.popBackStack() */ }
        }
    }

    private fun save() {
        val state = _formState.value

        // Validate
        val name = state.name.trim()
        if (name.isBlank()) {
            _formState.value = state.copy(nameError = "Name cannot be empty")
            return
        }

        val minutes = state.minutes.toIntOrNull() ?: 0
        val seconds = state.seconds.toIntOrNull() ?: 0
        val totalMs = AppUtil.minSecToMillis(minutes, seconds)
        if (totalMs <= 0L) {
            _formState.value = state.copy(timeError = "Time must be greater than zero")
            return
        }

        val increment = (state.incrementSec.toIntOrNull() ?: 0) * 1_000L
        val delay     = state.delaySec.toLongOrNull()?.let { it * 1_000L }

        _formState.value = _formState.value.copy(isSaving = true)

        viewModelScope.launch {
            database.insertAll(
                listOf(
                    ClockFormat(
                        // id = 0 → Room auto-generates
                        id        = 0,
                        name      = name,
                        countDown = totalMs,
                        increment = increment,
                        delay     = delay,
                    )
                )
            )
            eventChannel.trySend(AddTimerEvent.SavedAndNavigateBack)
        }
    }
}