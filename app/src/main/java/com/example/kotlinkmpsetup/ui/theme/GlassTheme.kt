package com.example.kotlinkmpsetup.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Professional Dark Night Colors
val DarkBackground = Color(0xFF070B19)
val DarkSurfaceGlass = Color(0xFF0F172A)
val DarkTextPrimary = Color(0xFFF8FAFC)
val DarkTextSecondary = Color(0xFF94A3B8)
val DarkBorderGlass = Color(0x38FFFFFF)
val DarkAccentPrimary = Color(0xFF38BDF8)   // Electric Sky Cyan
val DarkAccentSecondary = Color(0xFF818CF8) // Neon Indigo
val DarkAccentTertiary = Color(0xFFF43F5E)  // Coral Rose

// Professional Light Pearl Glass Colors
val LightBackground = Color(0xFFF1F5F9)
val LightSurfaceGlass = Color(0xEBFFFFFF)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF64748B)
val LightBorderGlass = Color(0x4094A3B8)
val LightAccentPrimary = Color(0xFF0284C7)   // Sapphire Blue
val LightAccentSecondary = Color(0xFF6366F1) // Indigo
val LightAccentTertiary = Color(0xFFE11D48)  // Crimson Rose

@Immutable
data class GlassColors(
    val isDark: Boolean,
    val background: Color,
    val glassSurface: Color,
    val glassBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accentPrimary: Color,
    val accentSecondary: Color,
    val accentTertiary: Color,
    val inputBackground: Color,
    val socialButtonBg: Color
)

val LocalGlassColors = compositionLocalOf {
    GlassColors(
        isDark = true,
        background = DarkBackground,
        glassSurface = DarkSurfaceGlass,
        glassBorder = DarkBorderGlass,
        textPrimary = DarkTextPrimary,
        textSecondary = DarkTextSecondary,
        accentPrimary = DarkAccentPrimary,
        accentSecondary = DarkAccentSecondary,
        accentTertiary = DarkAccentTertiary,
        inputBackground = Color.White.copy(alpha = 0.08f),
        socialButtonBg = Color.White.copy(alpha = 0.12f)
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = DarkAccentPrimary,
    secondary = DarkAccentSecondary,
    tertiary = DarkAccentTertiary,
    background = DarkBackground,
    surface = DarkSurfaceGlass,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = LightAccentPrimary,
    secondary = LightAccentSecondary,
    tertiary = LightAccentTertiary,
    background = LightBackground,
    surface = LightSurfaceGlass,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary
)

@Composable
fun LiquidGlassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val glassColors = remember(darkTheme) {
        if (darkTheme) {
            GlassColors(
                isDark = true,
                background = DarkBackground,
                glassSurface = Color(0xFF0F172A).copy(alpha = 0.65f),
                glassBorder = Color.White.copy(alpha = 0.22f),
                textPrimary = DarkTextPrimary,
                textSecondary = DarkTextSecondary,
                accentPrimary = DarkAccentPrimary,
                accentSecondary = DarkAccentSecondary,
                accentTertiary = DarkAccentTertiary,
                inputBackground = Color.White.copy(alpha = 0.08f),
                socialButtonBg = Color.White.copy(alpha = 0.12f)
            )
        } else {
            GlassColors(
                isDark = false,
                background = LightBackground,
                glassSurface = Color.White.copy(alpha = 0.72f),
                glassBorder = Color(0xFF94A3B8).copy(alpha = 0.40f),
                textPrimary = LightTextPrimary,
                textSecondary = LightTextSecondary,
                accentPrimary = LightAccentPrimary,
                accentSecondary = LightAccentSecondary,
                accentTertiary = LightAccentTertiary,
                inputBackground = Color(0xFFF1F5F9).copy(alpha = 0.85f),
                socialButtonBg = Color.White.copy(alpha = 0.90f)
            )
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
        }
    }

    CompositionLocalProvider(LocalGlassColors provides glassColors) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = Typography,
            content = content
        )
    }
}
