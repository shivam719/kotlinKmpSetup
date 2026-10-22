package com.example.kotlinkmpsetup.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
 * REUSABLE UI: LIQUID GLASS SHIMMER SKELETON MODIFIER & COMPONENTS
 * ====================================================================================
 * Provides animated shimmer skeleton placeholders for lists, cards, and items during loading states.
 *
 * INTERVIEW POINTS COVERED:
 * 1. **Phase-Deferred Animation Execution**: Uses `.drawWithContent` to read animation offsets in Draw Phase.
 * 2. **Modifier Extension API**: Extension function `Modifier.glassShimmer()` for composable containers.
 * 3. **Skeleton Loading UX**: Improves perceived loading performance over plain spinners.
 */
@Composable
fun Modifier.glassShimmer(
    shape: RoundedCornerShape = RoundedCornerShape(16.dp)
): Modifier {
    val glassColors = LocalGlassColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "glassShimmerTransition")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1.2f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    return this
        .clip(shape)
        .background(glassColors.glassSurface, shape)
        .border(1.dp, glassColors.glassBorder.copy(alpha = 0.3f), shape)
        .drawWithContent {
            drawContent()
            val width = size.width
            val startX = shimmerOffset * width
            val endX = (shimmerOffset + 0.6f) * width

            val highlightColor = if (glassColors.isDark) {
                Color.White.copy(alpha = 0.18f)
            } else {
                Color.White.copy(alpha = 0.55f)
            }

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        highlightColor,
                        Color.Transparent
                    ),
                    startX = startX,
                    endX = endX
                )
            )
        }
}

/**
 * Single Shimmer Skeleton Item mimicking list row.
 */
@Composable
fun GlassListItemShimmer(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .glassShimmer(RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Circle Avatar Skeleton
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Title Line
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle Line
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.40f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                )
            }
        }
    }
}

/**
 * Reusable Shimmer Skeleton List showing multiple list skeleton rows.
 */
@Composable
fun GlassListShimmer(
    modifier: Modifier = Modifier,
    itemCount: Int = 4,
    spacing: Dp = 12.dp
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        repeat(itemCount) {
            GlassListItemShimmer()
        }
    }
}

/**
 * Reusable Shimmer Skeleton Card for dashboard cards & forms.
 */
@Composable
fun GlassCardShimmer(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .glassShimmer(RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(Color.White.copy(alpha = 0.12f))
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.25f))
            )
        }
    }
}
