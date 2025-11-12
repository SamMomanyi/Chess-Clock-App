package com.example.chess_clock.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.chess_clock.AppUtils.ColorScheme
import com.example.chess_clock.AppUtils.PlayerState


private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

 val ActiveColorScheme = ColorScheme(
    borderColor = Color.White,
    contentColor = Color.Black,
    backGroundColor = Color.Yellow,
    activeIcon = Icons.Default.Face
)

 val InactiveColorScheme = ColorScheme(
    borderColor = Color.Gray,
    contentColor = Color.White,
    backGroundColor = Color.Gray,
    activeIcon = null
)

 val DefeatedColorScheme = ColorScheme(
    borderColor = Color.White,
    contentColor = Color.White,
    backGroundColor = Color.Red,
    activeIcon = null
)

@Composable
fun Chess_clockTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

fun PlayerState.toColorScheme() : ColorScheme = when(this){
    PlayerState.ACTIVE -> ActiveColorScheme
    PlayerState.INACTIVE -> InactiveColorScheme
    PlayerState.DEFEATED -> DefeatedColorScheme
}
