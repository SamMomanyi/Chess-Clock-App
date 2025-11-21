package com.example.chess_clock.model.database.clocks

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "ClocksTable")
data class ClockFormat(

    @PrimaryKey(autoGenerate = true)
    val id : Int = 1,
    val name : String = ". Rapid 10|0 ",
    var delay : Long? = 0L,
    var countDown : Long ,
    val increment : Long?,

)