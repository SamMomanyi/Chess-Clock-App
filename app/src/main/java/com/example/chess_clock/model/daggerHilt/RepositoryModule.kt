package com.example.chess_clock.model.daggerHilt

import com.example.chess_clock.model.database.clocks.ClocksDao
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule ()  {
    @Binds
    @Singleton
    abstract fun bindDBImplemention(
        myRepository : MyRepositoryImplementation
    ): ClocksDao
}