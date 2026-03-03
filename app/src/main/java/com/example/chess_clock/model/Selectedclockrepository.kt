package com.example.chess_clock.model

import com.example.chess_clock.AppUtils.AppUtil
import com.example.chess_clock.model.database.clocks.ClockFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SelectedClockRepository @Inject constructor() {

    private val _selectedClock = MutableStateFlow(AppUtil.predefinedClockFormats.first())
    val selectedClock: StateFlow<ClockFormat> = _selectedClock

    fun selectClock(clock: ClockFormat) {
        _selectedClock.value = clock
    }
}