package com.leosoft.smokefree.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Color(0xFFFF5C6C),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE84B5A),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF2F80ED),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF2ECC71),
    onTertiary = Color(0xFFFFFFFF),
    error = Color(0xFFEB5757),
    onError = Color(0xFFFFFFFF),
    background = Color(0xFFFAFAFB),
    onBackground = Color(0xFF111827),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFE5E7EB),
    onSurfaceVariant = Color(0xFF6B7280),
    outline = Color(0xFFE5E7EB)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFF8B97),
    onPrimary = Color(0xFF1F0B0D),
    primaryContainer = Color(0xFFE84B5A),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF6BA4F7),
    onSecondary = Color(0xFF0B1A2F),
    tertiary = Color(0xFF2ECC71),
    onTertiary = Color(0xFF0C2014),
    error = Color(0xFFEB5757),
    onError = Color(0xFF1E0B0B),
    background = Color(0xFF0F1115),
    onBackground = Color(0xFFE5E7EB),
    surface = Color(0xFF161A22),
    onSurface = Color(0xFFE5E7EB),
    surfaceVariant = Color(0xFF2A2F3A),
    onSurfaceVariant = Color(0xFF9CA3AF),
    outline = Color(0xFF2A2F3A)
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.SemiBold),
    headlineMedium = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
    titleLarge = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
    labelMedium = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium)
)

private val AppShapes = androidx.compose.material3.Shapes(
    small = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(26.dp)
)

object AppSpacing {
    val xs = 6.dp
    val s = 10.dp
    val m = 16.dp
    val l = 22.dp
    val xl = 28.dp
}

@Composable
fun SmokeFreeTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    SideEffect {
        val window = (view.context as android.app.Activity).window
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
