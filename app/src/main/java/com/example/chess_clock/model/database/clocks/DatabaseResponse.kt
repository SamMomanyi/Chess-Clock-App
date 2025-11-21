package com.example.chess_clock.model.database.clocks

sealed class DatabaseResponse<out T>{
data class Success<out T>(val data: T) : DatabaseResponse<T>()
data class Failed(val ex: Exception?) : DatabaseResponse<Nothing>()
data object isLoading : DatabaseResponse<Nothing>()}