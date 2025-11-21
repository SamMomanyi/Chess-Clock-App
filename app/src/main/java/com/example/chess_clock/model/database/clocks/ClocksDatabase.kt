package com.example.chess_clock.model.database.clocks

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ClockFormat::class],
    version = 1,
    exportSchema = false
)
abstract class ClocksDatabase: RoomDatabase() {

    abstract val dao : ClocksDao
}