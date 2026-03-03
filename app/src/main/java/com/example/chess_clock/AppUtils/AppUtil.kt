package com.example.chess_clock.AppUtils

import com.example.chess_clock.model.database.clocks.ClockFormat

object AppUtil {
    val predefinedClockFormats: List<ClockFormat> = mutableListOf(
        ClockFormat(id = 1, name = "• Rapid: 10|0",     delay = null, countDown = 600_000L,   increment = 0L),
        ClockFormat(id = 2, name = "• Blitz: 3|2",      delay = null, countDown = 180_000L,   increment = 2_000L),
        ClockFormat(id = 3, name = "• Rapid: 15|10",    delay = null, countDown = 900_000L,   increment = 10_000L),
        ClockFormat(id = 4, name = "• Classical: 30|0", delay = null, countDown = 1_800_000L, increment = 0L),
        ClockFormat(id = 5, name = "• Blitz: 5|5",      delay = null, countDown = 300_000L,   increment = 5_000L),
        ClockFormat(id = 6, name = "• Blitz: 5|0",      delay = null, countDown = 300_000L,   increment = 0L),
    )

    fun formatTime(time: Long): String {
        val minutes = time / 60_000
        val seconds = (time % 60_000) / 1_000
        return String.format("%02d:%02d", minutes, seconds)
    }

    /** Convert milliseconds to (minutes, seconds) for form fields */
    fun millisToMinSec(ms: Long): Pair<Int, Int> =
        (ms / 60_000).toInt() to ((ms % 60_000) / 1_000).toInt()

    /** Convert form fields back to milliseconds */
    fun minSecToMillis(minutes: Int, seconds: Int): Long =
        (minutes * 60_000L) + (seconds * 1_000L)
}

// ── Navigation routes ────────────────────────────────────────────────────────

object routes {
    const val screenA = "Timer_Screen"
    const val screenB = "Timer_Selection"
    const val screenC = "Settings_Screen"
    const val screenD = "EditTimerScreen/{clockId}"   // needs clockId arg
    const val screenE = "AddTimerScreen"

    /** Build a concrete edit route with a real ID */
    fun editTimer(clockId: Int) = "EditTimerScreen/$clockId"
}