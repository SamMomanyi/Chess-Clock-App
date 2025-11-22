package com.example.chess_clock.model.database.clocks

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.chess_clock.AppUtils.AppUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ClockFormat::class],
    version = 1,
    exportSchema = false
)
abstract class ClocksDatabase: RoomDatabase() {
    abstract val dao : ClocksDao

    companion object {
        @Volatile
        private var INSTANCE : ClocksDatabase? = null

        fun getInstance(context : Context): ClocksDatabase{
            return INSTANCE ?: synchronized(this){
                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        ClocksDatabase::class.java,
                        "ClocksTable"
                    )
                        .addCallback(seedClockFormats())
                        .build()

                INSTANCE = instance
                instance
            }
        }

        private fun seedClockFormats():Callback {
            return object : Callback(){
                override fun onCreate(db : SupportSQLiteDatabase){

                    //Insert predefined values on a background thread
                    CoroutineScope(Dispatchers.IO).launch {
                        INSTANCE?.dao?.insertAll(AppUtil.predefinedClockFormats)
                    }
                }
            }
        }
    }
}