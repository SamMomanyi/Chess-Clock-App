package com.example.chess_clock.model.daggerHilt.di

import android.app.Application
import android.content.Context
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext


@HiltAndroidApp
class AppContext : Application() {

    companion object{
        private lateinit var instance : AppContext

        fun getContext() : Context = instance.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}