package com.example.chess_clock.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.chess_clock.AppUtils.ColorScheme
import com.example.chess_clock.AppUtils.PlayerState

// ── Theme enum ────────────────────────────────────────────────────────────────

enum class AppTheme(val displayName: String) {
    MIDNIGHT_STEEL("Midnight Steel"),
    CLASSIC_WOOD("Classic Wood"),
    ROYAL_GOLD("Royal Gold"),
    EMERALD("Emerald"),
}

// ── Per-theme Material color schemes ─────────────────────────────────────────

private fun midnightSteelColorScheme() = darkColorScheme(
    primary          = MidnightSteel.Accent,
    onPrimary        = MidnightSteel.OnAccent,
    background       = MidnightSteel.Background,
    onBackground     = MidnightSteel.TextPrimary,
    surface          = MidnightSteel.Surface,
    onSurface        = MidnightSteel.TextPrimary,
    surfaceVariant   = MidnightSteel.SurfaceHigh,
    onSurfaceVariant = MidnightSteel.TextSecondary,
    primaryContainer = MidnightSteel.Active,
    secondary        = MidnightSteel.TextSecondary,
)

private fun classicWoodColorScheme() = darkColorScheme(
    primary          = ClassicWood.Accent,
    onPrimary        = ClassicWood.OnAccent,
    background       = ClassicWood.Background,
    onBackground     = ClassicWood.TextPrimary,
    surface          = ClassicWood.Surface,
    onSurface        = ClassicWood.TextPrimary,
    surfaceVariant   = ClassicWood.SurfaceHigh,
    onSurfaceVariant = ClassicWood.TextSecondary,
    primaryContainer = ClassicWood.Active,
    secondary        = ClassicWood.TextSecondary,
)

private fun royalGoldColorScheme() = darkColorScheme(
    primary          = RoyalGold.Accent,
    onPrimary        = RoyalGold.OnAccent,
    background       = RoyalGold.Background,
    onBackground     = RoyalGold.TextPrimary,
    surface          = RoyalGold.Surface,
    onSurface        = RoyalGold.TextPrimary,
    surfaceVariant   = RoyalGold.SurfaceHigh,
    onSurfaceVariant = RoyalGold.TextSecondary,
    primaryContainer = RoyalGold.Active,
    secondary        = RoyalGold.TextSecondary,
)

private fun emeraldColorScheme() = darkColorScheme(
    primary          = Emerald.Accent,
    onPrimary        = Emerald.OnAccent,
    background       = Emerald.Background,
    onBackground     = Emerald.TextPrimary,
    surface          = Emerald.Surface,
    onSurface        = Emerald.TextPrimary,
    surfaceVariant   = Emerald.SurfaceHigh,
    onSurfaceVariant = Emerald.TextSecondary,
    primaryContainer = Emerald.Active,
    secondary        = Emerald.TextSecondary,
)

// ── Per-theme player color schemes ────────────────────────────────────────────
// These replace the old hardcoded Active/Inactive/DefeatedColorScheme values.
// They are read by PlayerState.toColorScheme() which now needs the AppTheme.

fun activeColorScheme(theme: AppTheme) = when (theme) {
    AppTheme.MIDNIGHT_STEEL -> ColorScheme(
        borderColor     = MidnightSteel.Accent,
        contentColor    = MidnightSteel.TextPrimary,
        backGroundColor = MidnightSteel.Active,
        activeIcon      = Icons.Default.Face,
    )
    AppTheme.CLASSIC_WOOD -> ColorScheme(
        borderColor     = ClassicWood.Accent,
        contentColor    = ClassicWood.TextPrimary,
        backGroundColor = ClassicWood.Active,
        activeIcon      = Icons.Default.Face,
    )
    AppTheme.ROYAL_GOLD -> ColorScheme(
        borderColor     = RoyalGold.Accent,
        contentColor    = RoyalGold.TextPrimary,
        backGroundColor = RoyalGold.Active,
        activeIcon      = Icons.Default.Face,
    )
    AppTheme.EMERALD -> ColorScheme(
        borderColor     = Emerald.Accent,
        contentColor    = Emerald.TextPrimary,
        backGroundColor = Emerald.Active,
        activeIcon      = Icons.Default.Face,
    )
}

fun inactiveColorScheme(theme: AppTheme) = when (theme) {
    AppTheme.MIDNIGHT_STEEL -> ColorScheme(
        borderColor     = MidnightSteel.SurfaceHigh,
        contentColor    = MidnightSteel.TextSecondary,
        backGroundColor = MidnightSteel.Inactive,
        activeIcon      = null,
    )
    AppTheme.CLASSIC_WOOD -> ColorScheme(
        borderColor     = ClassicWood.SurfaceHigh,
        contentColor    = ClassicWood.TextSecondary,
        backGroundColor = ClassicWood.Inactive,
        activeIcon      = null,
    )
    AppTheme.ROYAL_GOLD -> ColorScheme(
        borderColor     = RoyalGold.SurfaceHigh,
        contentColor    = RoyalGold.TextSecondary,
        backGroundColor = RoyalGold.Inactive,
        activeIcon      = null,
    )
    AppTheme.EMERALD -> ColorScheme(
        borderColor     = Emerald.SurfaceHigh,
        contentColor    = Emerald.TextSecondary,
        backGroundColor = Emerald.Inactive,
        activeIcon      = null,
    )
}

fun defeatedColorScheme(theme: AppTheme) = when (theme) {
    AppTheme.MIDNIGHT_STEEL -> ColorScheme(
        borderColor     = Color.Red,
        contentColor    = MidnightSteel.TextPrimary,
        backGroundColor = MidnightSteel.Defeated,
        activeIcon      = null,
    )
    AppTheme.CLASSIC_WOOD -> ColorScheme(
        borderColor     = Color.Red,
        contentColor    = ClassicWood.TextPrimary,
        backGroundColor = ClassicWood.Defeated,
        activeIcon      = null,
    )
    AppTheme.ROYAL_GOLD -> ColorScheme(
        borderColor     = Color.Red,
        contentColor    = RoyalGold.TextPrimary,
        backGroundColor = RoyalGold.Defeated,
        activeIcon      = null,
    )
    AppTheme.EMERALD -> ColorScheme(
        borderColor     = Color.Red,
        contentColor    = Emerald.TextPrimary,
        backGroundColor = Emerald.Defeated,
        activeIcon      = null,
    )
}

// Returns the accent glow color for the pulsing border animation
fun accentGlowColor(theme: AppTheme): Color = when (theme) {
    AppTheme.MIDNIGHT_STEEL -> MidnightSteel.AccentGlow
    AppTheme.CLASSIC_WOOD   -> ClassicWood.AccentGlow
    AppTheme.ROYAL_GOLD     -> RoyalGold.AccentGlow
    AppTheme.EMERALD        -> Emerald.AccentGlow
}

fun accentColor(theme: AppTheme): Color = when (theme) {
    AppTheme.MIDNIGHT_STEEL -> MidnightSteel.Accent
    AppTheme.CLASSIC_WOOD   -> ClassicWood.Accent
    AppTheme.ROYAL_GOLD     -> RoyalGold.Accent
    AppTheme.EMERALD        -> Emerald.Accent
}

// Kept for TimerSelection and other screens that reference these directly
val ActiveColorScheme  = ColorScheme(
    borderColor     = MidnightSteel.Accent,
    contentColor    = MidnightSteel.TextPrimary,
    backGroundColor = MidnightSteel.Active,
    activeIcon      = Icons.Default.Face,
)
val InactiveColorScheme = ColorScheme(
    borderColor     = MidnightSteel.SurfaceHigh,
    contentColor    = MidnightSteel.TextSecondary,
    backGroundColor = MidnightSteel.Inactive,
    activeIcon      = null,
)

// ── Extension on PlayerState ──────────────────────────────────────────────────
// Old single-arg version kept so existing call sites still compile.
// HomeScreenViewModel should call the two-arg version below instead.

fun PlayerState.toColorScheme(): ColorScheme = toColorScheme(AppTheme.MIDNIGHT_STEEL)

fun PlayerState.toColorScheme(theme: AppTheme): ColorScheme = when (this) {
    PlayerState.ACTIVE   -> activeColorScheme(theme)
    PlayerState.INACTIVE -> inactiveColorScheme(theme)
    PlayerState.DEFEATED -> defeatedColorScheme(theme)
}

// ── Root composable ───────────────────────────────────────────────────────────

@Composable
fun Chess_clockTheme(
    appTheme: AppTheme = AppTheme.MIDNIGHT_STEEL,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.MIDNIGHT_STEEL -> midnightSteelColorScheme()
        AppTheme.CLASSIC_WOOD   -> classicWoodColorScheme()
        AppTheme.ROYAL_GOLD     -> royalGoldColorScheme()
        AppTheme.EMERALD        -> emeraldColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}