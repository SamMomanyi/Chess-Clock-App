package com.example.chess_clock.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chess_clock.model.ThemeRepository
import com.example.chess_clock.ui.theme.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    val currentTheme = themeRepository.selectedTheme.stateIn(
        scope         = viewModelScope,
        started       = SharingStarted.Eagerly,   // needs to be ready before first frame
        initialValue  = AppTheme.MIDNIGHT_STEEL
    )

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            themeRepository.setTheme(theme)
        }
    }
}