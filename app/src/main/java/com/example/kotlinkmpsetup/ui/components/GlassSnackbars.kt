package com.example.kotlinkmpsetup.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinkmpsetup.ui.theme.LocalGlassColors
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * Type variants for [GlassSnackbar].
 */
enum class SnackbarType {
    Success,
    Error,
    Warning,
    Info
}

/**
 * Encapsulates Glass Toast state.
 */
data class GlassToastState(
    val message: String = "",
    val type: SnackbarType = SnackbarType.Info,
    val isVisible: Boolean = false
)

/**
 * Controller class to trigger custom glass toast notifications.
 */
class GlassToastController(
    val state: () -> GlassToastState,
    val show: (message: String, type: SnackbarType) -> Unit,
    val hide: () -> Unit
)

/**
 * Rememberable Glass Toast Controller that automatically auto-dismisses after [durationMillis].
 */
@Composable
fun rememberGlassToastState(durationMillis: Long = 3200L): GlassToastController {
    var toastState by remember { mutableStateOf(GlassToastState()) }

    LaunchedEffect(toastState.isVisible) {
        if (toastState.isVisible) {
            delay(durationMillis.milliseconds)
            toastState = toastState.copy(isVisible = false)
        }
    }

    return remember {
        GlassToastController(
            state = { toastState },
            show = { msg, type ->
                toastState = GlassToastState(message = msg, type = type, isVisible = true)
            },
            hide = {
                toastState = toastState.copy(isVisible = false)
            }
        )
    }
}

/**
 * ====================================================================================
 * REUSABLE UI: CUSTOM GLASSMorphic TOAST / SNACKBAR
 * ====================================================================================
 * A floating glassmorphic notification banner for displaying Success, Error, Warning, or Info.
 * Replaces standard Android Toast with custom animated glass toast.
 */
@Composable
fun GlassSnackbar(
    message: String,
    visible: Boolean,
    modifier: Modifier = Modifier,
    type: SnackbarType = SnackbarType.Info
) {
    val glassColors = LocalGlassColors.current

    val (icon, accentColor) = when (type) {
        SnackbarType.Success -> Pair(Icons.Default.CheckCircle, Color(0xFF10B981))
        SnackbarType.Error -> Pair(Icons.Default.Error, glassColors.accentTertiary)
        SnackbarType.Warning -> Pair(Icons.Default.Warning, Color(0xFFF59E0B))
        SnackbarType.Info -> Pair(Icons.Default.Info, glassColors.accentPrimary)
    }

    AnimatedVisibility(
        visible = visible && message.isNotBlank(),
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut(),
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(glassColors.glassSurface)
                .border(1.2.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = type.name,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = message,
                    color = glassColors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
