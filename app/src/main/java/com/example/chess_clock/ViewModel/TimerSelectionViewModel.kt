package com.example.chess_clock.ViewModel

import androidx.lifecycle.ViewModel
import com.example.chess_clock.AppUtils.TimerSelectionCommands
import com.example.chess_clock.AppUtils.TimerSelectionState
import com.example.chess_clock.model.daggerHilt.MyRepositoryImplementation
import com.example.chess_clock.model.database.clocks.ClockFormat
import com.example.chess_clock.model.database.clocks.DatabaseResponse
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class TimerSelectionViewModel @Inject constructor(
    private val database: MyRepositoryImplementation
) : ViewModel() {


    private val databaseResponse = MutableStateFlow<DatabaseResponse<List<ClockFormat>>>(
        value = DatabaseResponse.isLoading
    )
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

    private val state = combine(
        databaseResponse,_uistate,selectedClockFormat
    ){ db,current,selected ->
        when(db) {
            is DatabaseResponse.Failed -> {
                current.copy(
                    isLoading = false,
                    error = db.ex?.message ?: "Failed to load clocks"
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
    }

    fun TimerSelectionCommandHandler(Command: TimerSelectionCommands) {
        when (Command) {
            TimerSelectionCommands.AddTimeClicked -> {

            }

            is TimerSelectionCommands.CardClicked -> {

            }

            TimerSelectionCommands.EditTimerClicked -> {

            }

            TimerSelectionCommands.StartButtonClicked -> {

            }
        }
    }
}