package com.example.chess_clock.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chess_clock.AppUtils.TimeScreenState
import com.example.chess_clock.AppUtils.TimerSelectionCommands
import com.example.chess_clock.AppUtils.TimerSelectionState
import com.example.chess_clock.model.daggerHilt.MyRepositoryImplementation
import com.example.chess_clock.model.database.clocks.ClockFormat
import com.example.chess_clock.model.database.clocks.DatabaseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerSelectionViewModel @Inject constructor(
    private val database: MyRepositoryImplementation
) : ViewModel() {

    private val databaseResponse =
        MutableStateFlow<DatabaseResponse<List<ClockFormat>>>(value = DatabaseResponse.isLoading)
    val dbResponse: StateFlow<DatabaseResponse<List<ClockFormat>>> = databaseResponse

    //know we have to listen to UI data and equate something to database response
    init {
        viewModelScope.launch {
            database.getAllClockFormats()
                .onStart { databaseResponse.value = DatabaseResponse.isLoading }
                .catch { ex -> databaseResponse.value = DatabaseResponse.Failed(ex.message) }
                .collect { data -> databaseResponse.value = DatabaseResponse.Success(data) }
        }
    }

    private val _uistate = MutableStateFlow<TimerSelectionState>(TimerSelectionState())
    private val selectedClockFormat = MutableStateFlow<ClockFormat>(
        ClockFormat(
            id = 2,
            name = "• Blitz: 3|2",
            delay = null,
            countDown = 180_000L,
            increment = 2_000L,
        )
    )

    val state = combine(
        databaseResponse, _uistate, selectedClockFormat
    ) { db, current, selected ->
        when (db) {
            is DatabaseResponse.Failed -> {
                current.copy(
                    isLoading = false,
                    error = db.message ?: "Failed to load clocks"
                )
            }

            is DatabaseResponse.Success<List<ClockFormat>> -> {
                current.copy(
                    clocks = db.data,
                    isLoading = false,
                    error = null,
                    selectedClock = selected
                )
            }

            DatabaseResponse.isLoading -> {
                current.copy(
                    isLoading = true,
                    error = null
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), TimerSelectionState())


    fun TimerSelectionCommandHandler(Command: TimerSelectionCommands) {
        when (Command) {
            TimerSelectionCommands.AddTimeClicked -> {

            }

            is TimerSelectionCommands.CardClicked -> {
                selectedClockFormat.value = Command.clock
            }

            TimerSelectionCommands.EditTimerClicked -> {

            }

            is TimerSelectionCommands.StartButtonClicked -> {

            }
        }
    }
}