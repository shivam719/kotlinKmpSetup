package com.example.kotlinkmpsetup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.kotlinkmpsetup.ui.theme.LocalGlassColors

/**
 * Custom glassmorphism modifier for adding translucent glass surface,
 * specular border highlight, and inner light sheen.
 */
fun Modifier.glassmorphic(
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color,
    borderColor: Color
): Modifier = this
    .clip(shape)
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                backgroundColor.copy(alpha = (backgroundColor.alpha * 1.15f).coerceAtMost(0.95f)),
                backgroundColor,
                backgroundColor.copy(alpha = (backgroundColor.alpha * 0.85f).coerceAtLeast(0.10f))
            )
        ),
        shape = shape
    )
    .border(
        width = 1.2.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                borderColor,
                borderColor.copy(alpha = 0.20f),
                borderColor.copy(alpha = 0.50f),
                Color.Transparent
            )
        ),
        shape = shape
    )

/**
 * Premium Liquid Glass Card container for forms, login, signup, and hero banners.
 * Automatically adapts between Light Mode & Dark Night Mode.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    contentPadding: Dp = 24.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val glassColors = LocalGlassColors.current

    Box(
        modifier = modifier
            .shadow(
                elevation = if (glassColors.isDark) 16.dp else 10.dp,
                shape = shape,
                ambientColor = glassColors.accentPrimary.copy(alpha = if (glassColors.isDark) 0.25f else 0.10f),
                spotColor = glassColors.accentSecondary.copy(alpha = if (glassColors.isDark) 0.35f else 0.15f)
            )
            .glassmorphic(
                shape = shape,
                backgroundColor = glassColors.glassSurface,
                borderColor = glassColors.glassBorder
            )
    ) {
        // Specular Top Light Reflection Layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            if (glassColors.isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        endY = 160f
                    ),
                    shape = shape
                )
        )

        // Unblurred Foreground Content Container
        Box(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}
