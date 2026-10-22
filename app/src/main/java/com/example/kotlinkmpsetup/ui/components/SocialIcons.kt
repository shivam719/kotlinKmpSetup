package com.example.kotlinkmpsetup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Crisp Vector Apple Logo Icon (eliminates missing font glyph box [?]).
 */
@Composable
fun AppleIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    size: Dp = 22.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val path = Path().apply {
            // Apple Body Path
            moveTo(w * 0.68f, h * 0.22f)
            cubicTo(w * 0.63f, h * 0.22f, w * 0.55f, h * 0.26f, w * 0.50f, h * 0.26f)
            cubicTo(w * 0.44f, h * 0.26f, w * 0.37f, h * 0.22f, w * 0.31f, h * 0.22f)
            cubicTo(w * 0.18f, h * 0.22f, w * 0.07f, h * 0.33f, w * 0.07f, h * 0.52f)
            cubicTo(w * 0.07f, h * 0.67f, w * 0.13f, h * 0.83f, w * 0.23f, h * 0.96f)
            cubicTo(w * 0.28f, h * 1.03f, w * 0.34f, h * 1.09f, w * 0.41f, h * 1.09f)
            cubicTo(w * 0.47f, h * 1.09f, w * 0.50f, h * 1.05f, h * 0.57f, h * 1.05f)
            cubicTo(w * 0.64f, h * 1.05f, w * 0.67f, h * 1.09f, w * 0.73f, h * 1.09f)
            cubicTo(w * 0.80f, h * 1.09f, w * 0.86f, h * 1.02f, w * 0.91f, h * 0.95f)
            cubicTo(w * 0.97f, h * 0.86f, w * 0.99f, h * 0.78f, w * 1.00f, h * 0.77f)
            cubicTo(w * 0.99f, h * 0.76f, w * 0.85f, h * 0.71f, w * 0.85f, h * 0.55f)
            cubicTo(w * 0.85f, h * 0.41f, w * 0.96f, h * 0.34f, w * 0.97f, h * 0.33f)
            cubicTo(w * 0.90f, h * 0.23f, w * 0.79f, h * 0.22f, w * 0.68f, h * 0.22f)
            close()
        }

        val leafPath = Path().apply {
            // Apple Leaf
            moveTo(w * 0.66f, h * 0.00f)
            cubicTo(w * 0.59f, h * 0.01f, w * 0.52f, h * 0.06f, w * 0.48f, h * 0.12f)
            cubicTo(w * 0.43f, h * 0.18f, w * 0.42f, h * 0.25f, w * 0.48f, h * 0.31f)
            cubicTo(w * 0.56f, h * 0.32f, w * 0.63f, h * 0.26f, w * 0.67f, h * 0.20f)
            cubicTo(w * 0.71f, h * 0.14f, w * 0.71f, h * 0.06f, w * 0.66f, h * 0.00f)
            close()
        }

        drawPath(path = path, color = tint)
        drawPath(path = leafPath, color = tint)
    }
}

/**
 * Clean Google "G" Icon.
 */
@Composable
fun GoogleIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val center = Offset(w / 2f, h / 2f)
        val radius = w / 2.3f

        // Draw Google G Ring Segments
        // Red Top
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 200f,
            sweepAngle = 100f,
            useCenter = false,
            size = Size(radius * 2, radius * 2),
            topLeft = Offset(center.x - radius, center.y - radius),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = w * 0.2f)
        )
        // Yellow Left
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 120f,
            sweepAngle = 80f,
            useCenter = false,
            size = Size(radius * 2, radius * 2),
            topLeft = Offset(center.x - radius, center.y - radius),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = w * 0.2f)
        )
        // Green Bottom
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 30f,
            sweepAngle = 90f,
            useCenter = false,
            size = Size(radius * 2, radius * 2),
            topLeft = Offset(center.x - radius, center.y - radius),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = w * 0.2f)
        )
        // Blue Right Bar
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = 300f,
            sweepAngle = 90f,
            useCenter = false,
            size = Size(radius * 2, radius * 2),
            topLeft = Offset(center.x - radius, center.y - radius),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = w * 0.2f)
        )
        // Horizontal bar
        drawRect(
            color = Color(0xFF4285F4),
            topLeft = Offset(center.x, center.y - w * 0.08f),
            size = Size(radius * 0.9f, w * 0.16f)
        )
    }
}

/**
 * Clean Facebook / Meta "f" Icon.
 */
@Composable
fun FacebookIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1877F2),
    size: Dp = 22.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val path = Path().apply {
            moveTo(w * 0.65f, h * 0.20f)
            lineTo(w * 0.80f, h * 0.20f)
            lineTo(w * 0.80f, h * 0.02f)
            cubicTo(w * 0.74f, h * 0.01f, w * 0.65f, h * 0.00f, w * 0.55f, h * 0.00f)
            cubicTo(w * 0.35f, h * 0.00f, w * 0.22f, h * 0.12f, w * 0.22f, h * 0.34f)
            lineTo(w * 0.22f, h * 0.48f)
            lineTo(w * 0.05f, h * 0.48f)
            lineTo(w * 0.05f, h * 0.68f)
            lineTo(w * 0.22f, h * 0.68f)
            lineTo(w * 0.22f, h * 1.00f)
            lineTo(w * 0.45f, h * 1.00f)
            lineTo(w * 0.45f, h * 0.68f)
            lineTo(w * 0.63f, h * 0.68f)
            lineTo(w * 0.66f, h * 0.48f)
            lineTo(w * 0.45f, h * 0.48f)
            lineTo(w * 0.45f, h * 0.37f)
            cubicTo(w * 0.45f, h * 0.30f, w * 0.48f, h * 0.25f, w * 0.58f, h * 0.25f)
            cubicTo(w * 0.61f, h * 0.25f, w * 0.63f, h * 0.25f, w * 0.65f, h * 0.20f)
            close()
        }

        drawPath(path = path, color = tint)
    }
}
