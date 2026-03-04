package com.example.chess_clock.model

import android.content.Context
import android.content.SharedPreferences
import com.example.chess_clock.ui.theme.AppTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("chess_clock_prefs", Context.MODE_PRIVATE)

    // Read whatever was saved last time, defaulting to Midnight Steel
    private val initial = AppTheme.valueOf(
        prefs.getString("selected_theme", AppTheme.MIDNIGHT_STEEL.name)
            ?: AppTheme.MIDNIGHT_STEEL.name
    )

    private val _selectedTheme = MutableStateFlow(initial)
    val selectedTheme: StateFlow<AppTheme> = _selectedTheme

    fun setTheme(theme: AppTheme) {
        _selectedTheme.value = theme
        // Persist so the choice survives app restarts
        prefs.edit().putString("selected_theme", theme.name).apply()
    }
}