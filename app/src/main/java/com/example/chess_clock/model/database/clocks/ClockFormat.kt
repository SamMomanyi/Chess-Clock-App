package com.example.chess_clock.model.database.clocks

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Timer

@Entity(tableName = "ClocksTable")
data class ClockFormat(

    @PrimaryKey(autoGenerate = true)
    val id : Int,
    var delay : Long?,
    var countDown : Long,
    val increment : Long?,
    val microSecondCountDown : Long

)