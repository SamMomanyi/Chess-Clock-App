package com.example.chess_clock.model.daggerHilt

import com.example.chess_clock.model.database.clocks.ClockFormat
import com.example.chess_clock.model.database.clocks.ClocksDao
import com.example.chess_clock.model.database.clocks.ClocksDatabase
import javax.inject.Inject


class MyRepositoryImplementation @Inject constructor(
    private val database : ClocksDatabase
):ClocksDao{

    private val dao = database.dao

    override suspend fun updateTimeFormat(format: ClockFormat) = dao.updateTimeFormat(format)
    override suspend fun deleteTimeFormat(format: ClockFormat) = dao.deleteTimeFormat(format)
    override suspend fun insertAll(format: List<ClockFormat>)  = dao.insertAll(format)


    override fun getAllClockFormats() = dao.getAllClockFormats()

    override fun getAllClockFormatsWithIncrements() = dao.getAllClockFormatsWithIncrements()

    override fun getAllClockFormatsWithDelays() = dao.getAllClockFormatsWithDelays()

}