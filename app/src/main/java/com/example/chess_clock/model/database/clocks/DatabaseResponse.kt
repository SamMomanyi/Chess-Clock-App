package com.example.chess_clock.model.database.clocks

sealed class DatabaseResponse<out : ClockFormat>
data class Success(val clocks : List<ClockFormat>) : DatabaseResponse<ClockFormat>()
data class Failed(val ex : Exception?) : DatabaseResponse<Nothing>()
data object isLoading : DatabaseResponse<Nothing>()