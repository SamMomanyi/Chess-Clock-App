package com.example.chess_clock.model.database.players

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PlayerEntities::class],
    version = 1,
    exportSchema = false
)
abstract class PlayerDatabase : RoomDatabase(){
    abstract val dao : PlayerDao
}