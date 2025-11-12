package com.example.chess_clock.model.daggerHilt.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.chess_clock.model.database.clocks.ClocksDatabase
import com.example.chess_clock.model.database.players.PlayerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMyTimeFormat( @ApplicationContext context: Context):ClocksDatabase{
       return Room.databaseBuilder(
           context,
           klass = ClocksDatabase::class.java,
           name = "time_formats"
       ).build()
    }

    @Provides
    @Singleton
    fun providePlayerName(@ApplicationContext app:Application):PlayerDatabase{
        return Room.databaseBuilder(
            context = app,
            klass = PlayerDatabase::class.java,
            name = "playername"
        ).build()
    }
}