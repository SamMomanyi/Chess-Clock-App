package com.example.chess_clock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.chess_clock.ViewModel.ThemeViewModel
import com.example.chess_clock.ui.theme.AppTheme
import com.example.chess_clock.ui.theme.ClassicWood
import com.example.chess_clock.ui.theme.Emerald
import com.example.chess_clock.ui.theme.MidnightSteel
import com.example.chess_clock.ui.theme.RoyalGold
import com.example.chess_clock.ui.theme.NeonCyber
import com.example.chess_clock.ui.theme.SunsetBlaze

// Preview swatches shown on each theme card
private data class ThemePreview(
    val theme: AppTheme,
    val background: Color,
    val accent: Color,
    val surface: Color,
    val description: String,
)

private val themePreviews = listOf(
    ThemePreview(
        theme       = AppTheme.MIDNIGHT_STEEL,
        background  = MidnightSteel.Background,
        accent      = MidnightSteel.Accent,
        surface     = MidnightSteel.Surface,
        description = "Dark navy with electric blue accents",
    ),
    ThemePreview(
        theme       = AppTheme.CLASSIC_WOOD,
        background  = ClassicWood.Background,
        accent      = ClassicWood.Accent,
        surface     = ClassicWood.Surface,
        description = "Warm walnut and amber tones",
    ),
    ThemePreview(
        theme       = AppTheme.ROYAL_GOLD,
        background  = RoyalGold.Background,
        accent      = RoyalGold.Accent,
        surface     = RoyalGold.Surface,
        description = "Sleek black with tournament gold",
    ),
    ThemePreview(
        theme       = AppTheme.EMERALD,
        background  = Emerald.Background,
        accent      = Emerald.Accent,
        surface     = Emerald.Surface,
        description = "Classic board green with ivory",
    ),
    ThemePreview(
        theme       = AppTheme.NEON_CYBER,
        background  = NeonCyber.Background,
        accent      = NeonCyber.Accent,
        surface     = NeonCyber.Surface,
        description = "Dark purple with vivid cyan glow",
    ),
    ThemePreview(
        theme       = AppTheme.SUNSET_BLAZE,
        background  = SunsetBlaze.Background,
        accent      = SunsetBlaze.Accent,
        surface     = SunsetBlaze.Surface,
        description = "Deep amber with blazing orange",
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel = hiltViewModel(),
) {
    val currentTheme by themeViewModel.currentTheme.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Settings", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            Spacer(Modifier.height(8.dp))

            Text(
                text       = "Appearance",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 13.sp,
                color      = MaterialTheme.colorScheme.primary,
                modifier   = Modifier.padding(bottom = 4.dp),
            )

            Text(
                text     = "Theme",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color    = MaterialTheme.colorScheme.onBackground,
            )

            Text(
                text     = "Choose a colour scheme for the app. Takes effect immediately.",
                fontSize = 13.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(8.dp))

            themePreviews.forEach { preview ->
                ThemeCard(
                    preview    = preview,
                    isSelected = currentTheme == preview.theme,
                    onClick    = { themeViewModel.setTheme(preview.theme) },
                )
            }
        }
    }
}

@Composable
private fun ThemeCard(
    preview: ThemePreview,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (isSelected) preview.accent else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(preview.surface)
            .border(2.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Colour swatches
            Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                Swatch(preview.background)
                Swatch(preview.accent)
                Swatch(preview.surface)
            }

            Spacer(Modifier.width(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text       = preview.theme.displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = Color.White,
                )
                Text(
                    text     = preview.description,
                    fontSize = 12.sp,
                    color    = Color.Gray,
                )
            }
        }

        if (isSelected) {
            Box(
                modifier         = Modifier
                    .size(28.dp)
                    .background(preview.accent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint               = Color.Black,
                    modifier           = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun Swatch(color: Color) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
    )
}