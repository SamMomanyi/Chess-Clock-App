package com.example.chess_clock.model.database.players

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PlayerTable")
data class PlayerEntities(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val name : String
)
