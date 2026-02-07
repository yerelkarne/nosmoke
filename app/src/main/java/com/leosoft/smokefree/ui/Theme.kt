package com.leosoft.smokefree.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Color(0xFF7E57C2),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF26A69A),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFFFFB74D),
    onTertiary = Color(0xFF3E2723),
    background = Color(0xFFF8F5FF),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFEDE7F6)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFD1C4E9),
    onPrimary = Color(0xFF2A1B3D),
    secondary = Color(0xFF80CBC4),
    onSecondary = Color(0xFF00332F),
    tertiary = Color(0xFFFFCC80),
    onTertiary = Color(0xFF3E2723),
    background = Color(0xFF1B1622),
    surface = Color(0xFF241D2E),
    surfaceVariant = Color(0xFF332A40)
)

@Composable
fun SmokeFreeTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    SideEffect {
        val window = (view.context as android.app.Activity).window
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    MaterialTheme(
        colorScheme = if (androidx.compose.foundation.isSystemInDarkTheme()) DarkColors else LightColors,
        content = content
    )
}
