package com.example.kotlinkmpsetup.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.kotlinkmpsetup.ui.theme.LocalGlassColors
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated organic fluid liquid background with radial gradient spheres.
 * Dynamically adjusts color intensity and transparency for Light Mode & Dark Night Mode.
 */
@Composable
fun LiquidBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val glassColors = LocalGlassColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "LiquidAnimation")

    // Blob 1 Phase
    val phase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase1"
    )

    // Blob 2 Phase
    val phase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase2"
    )

    // Blob 3 Phase
    val phase3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 22000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase3"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(glassColors.background)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val minDim = minOf(width, height)

            val alphaMultiplier = if (glassColors.isDark) 1.0f else 0.45f

            // Blob 1: Primary Accent (Sky Cyan / Sapphire Blue)
            val radius1 = minDim * 0.45f
            val x1 = width * 0.3f + sin(phase1) * width * 0.2f
            val y1 = height * 0.25f + cos(phase1 * 0.8f) * height * 0.15f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glassColors.accentPrimary.copy(alpha = 0.50f * alphaMultiplier),
                        glassColors.accentPrimary.copy(alpha = 0.20f * alphaMultiplier),
                        Color.Transparent
                    ),
                    center = Offset(x1, y1),
                    radius = radius1
                ),
                radius = radius1,
                center = Offset(x1, y1)
            )

            // Blob 2: Secondary Accent (Indigo / Purple)
            val radius2 = minDim * 0.50f
            val x2 = width * 0.7f + cos(phase2) * width * 0.22f
            val y2 = height * 0.7f + sin(phase2 * 0.9f) * height * 0.18f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glassColors.accentSecondary.copy(alpha = 0.45f * alphaMultiplier),
                        glassColors.accentSecondary.copy(alpha = 0.18f * alphaMultiplier),
                        Color.Transparent
                    ),
                    center = Offset(x2, y2),
                    radius = radius2
                ),
                radius = radius2,
                center = Offset(x2, y2)
            )

            // Blob 3: Tertiary Accent (Rose / Coral)
            val radius3 = minDim * 0.38f
            val x3 = width * 0.45f + sin(phase3 * 1.1f) * width * 0.25f
            val y3 = height * 0.55f + cos(phase3) * height * 0.2f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glassColors.accentTertiary.copy(alpha = 0.40f * alphaMultiplier),
                        glassColors.accentTertiary.copy(alpha = 0.12f * alphaMultiplier),
                        Color.Transparent
                    ),
                    center = Offset(x3, y3),
                    radius = radius3
                ),
                radius = radius3,
                center = Offset(x3, y3)
            )
        }

        content()
    }
}
