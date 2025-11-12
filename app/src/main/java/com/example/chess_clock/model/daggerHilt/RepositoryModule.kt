package com.example.chess_clock.model.daggerHilt

import com.example.chess_clock.model.database.clocks.ClocksDao
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule ()  {
    @Binds
    abstract fun bindDBImplemention(
        myRepository : MyRepositoryImplementation
    ): ClocksDao
}