package com.example.chess_clock.model.database.clocks

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ClocksDao {

    @Upsert
    suspend fun updateTimeFormat(format: ClockFormat)

    @Delete
    suspend fun deleteTimeFormat(format : ClockFormat)


    @Query("SELECT * FROM ClocksTable")
    fun getAllClockFormats() :  Flow<List<ClockFormat>>

    @Query("SELECT * FROM ClocksTable WHERE increment IS NOT NULL")
    fun getAllClockFormatsWithIncrements(): Flow<List<ClockFormat>>

    @Query("SELECT * FROM ClocksTable WHERE delay IS NOT NULL")
    fun getAllClockFormatsWithDelays(): Flow<List<ClockFormat>>


}