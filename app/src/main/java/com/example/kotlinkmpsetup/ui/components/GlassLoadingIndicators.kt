package com.example.kotlinkmpsetup.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.kotlinkmpsetup.ui.theme.LocalGlassColors

/**
 * ====================================================================================
 * REUSABLE UI: LIQUID GLASS LINEAR PROGRESS INDICATOR (LINE LOADING)
 * ====================================================================================
 * A sleek, high-performance top line progress loader that smoothly animates during
 * pending or loading states.
 *
 * INTERVIEW POINTS COVERED:
 * 1. **Phase-Deferred Animation**: Uses `.drawWithContent` to read animation state in Draw Phase.
 * 2. **Reusability**: Can be placed at the top of forms, cards, or screens.
 */
@Composable
fun GlassLinearProgressIndicator(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    height: Dp = 4.dp
) {
    val glassColors = LocalGlassColors.current

    AnimatedVisibility(
        visible = isLoading,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier.fillMaxWidth()
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "lineLoading")
        val progressOffset by infiniteTransition.animateFloat(
            initialValue = -0.5f,
            targetValue = 1.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "progressOffset"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(height / 2))
                .background(glassColors.glassBorder.copy(alpha = 0.2f))
                .drawWithContent {
                    drawContent()
                    val width = size.width
                    val startX = progressOffset * width
                    val endX = (progressOffset + 0.4f) * width

                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                glassColors.accentPrimary,
                                glassColors.accentSecondary,
                                glassColors.accentTertiary,
                                Color.Transparent
                            ),
                            startX = startX,
                            endX = endX
                        )
                    )
                }
        )
    }
}

/**
 * Alias function for [GlassLinearProgressIndicator] to support line loading naming.
 */
@Composable
fun LineLoading(
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    GlassLinearProgressIndicator(
        isLoading = isLoading,
        modifier = modifier
    )
}
