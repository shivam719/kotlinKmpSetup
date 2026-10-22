package com.example.kotlinkmpsetup.ui.components

import android.os.Build
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.example.kotlinkmpsetup.ui.theme.LocalGlassColors

/**
 * Status types supported by [GlassStatusDialog].
 */
enum class DialogStatusType {
    Success,
    Error,
    Pending,
    Processing,
    Warning,
    Info
}

/**
 * Enables hardware window blur behind the Dialog on Android 12+ (SDK 31+).
 */
@Composable
private fun EnableDialogWindowBlur() {
    val view = LocalView.current
    LaunchedEffect(view) {
        val window = (view.parent as? DialogWindowProvider)?.window
        if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
            window.attributes = window.attributes.apply {
                blurBehindRadius = 60
            }
        }
    }
}

/**
 * ====================================================================================
 * REUSABLE UI: CUSTOM GLASSMorphic STATUS DIALOG
 * ====================================================================================
 * A universal glassmorphic dialog with frosted dark glass backdrop & Android 12+ window blur.
 */
@Composable
fun GlassStatusDialog(
    statusType: DialogStatusType,
    title: String,
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmText: String = "OK",
    onConfirm: (() -> Unit)? = null,
    dismissText: String? = null,
    properties: DialogProperties = DialogProperties(
        dismissOnBackPress = true,
        dismissOnClickOutside = true,
        usePlatformDefaultWidth = false
    )
) {
    val glassColors = LocalGlassColors.current

    val (icon, tint, isAnimatedProgress) = when (statusType) {
        DialogStatusType.Success -> Triple(Icons.Default.CheckCircle, Color(0xFF10B981), false)
        DialogStatusType.Error -> Triple(Icons.Default.Error, glassColors.accentTertiary, false)
        DialogStatusType.Pending -> Triple(Icons.Default.HourglassTop, glassColors.accentSecondary, false)
        DialogStatusType.Processing -> Triple(null, glassColors.accentPrimary, true)
        DialogStatusType.Warning -> Triple(Icons.Default.Warning, Color(0xFFF59E0B), false)
        DialogStatusType.Info -> Triple(Icons.Default.Info, glassColors.accentPrimary, false)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = properties
    ) {
        EnableDialogWindowBlur()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            GlassCard(
                modifier = modifier
                    .padding(20.dp)
                    .fillMaxWidth(0.92f)
                    .widthIn(max = 420.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = true
                    ) { /* Catch clicks to prevent dismiss on card tap */ },
                contentPadding = 24.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Header Icon Sphere
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(tint.copy(alpha = 0.15f))
                            .border(1.5.dp, tint, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isAnimatedProgress) {
                            CircularProgressIndicator(
                                color = tint,
                                modifier = Modifier.size(36.dp),
                                strokeWidth = 3.dp
                            )
                        } else if (icon != null) {
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                tint = tint,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Title
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.textPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Message Description
                    Text(
                        text = message,
                        fontSize = 14.sp,
                        color = glassColors.textSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (dismissText != null) {
                            TextButton(onClick = onDismiss) {
                                Text(
                                    text = dismissText,
                                    color = glassColors.textSecondary,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        GlassButton(
                            text = confirmText,
                            onClick = {
                                onConfirm?.invoke()
                                onDismiss()
                            },
                            modifier = Modifier.height(46.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * ====================================================================================
 * REUSABLE UI: GLASSMorphic OTP ENTRY DIALOG (2-FACTOR AUTHENTICATION)
 * ====================================================================================
 * Displays OTP entry dialog when API returns `statuscode == 2`.
 */
@Composable
fun GlassOtpDialog(
    onDismiss: () -> Unit,
    onVerifyOtp: (otp: String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var otpText by rememberSaveable { mutableStateOf("") }
    val glassColors = LocalGlassColors.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        EnableDialogWindowBlur()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f)),
            contentAlignment = Alignment.Center
        ) {
            GlassCard(
                modifier = modifier
                    .padding(20.dp)
                    .fillMaxWidth(0.92f)
                    .widthIn(max = 400.dp),
                contentPadding = 24.dp
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(glassColors.accentPrimary.copy(alpha = 0.15f))
                            .border(1.5.dp, glassColors.accentPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "OTP Verification",
                            tint = glassColors.accentPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Verify OTP",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Please enter the OTP verification code sent to your registered mobile number.",
                        fontSize = 13.sp,
                        color = glassColors.textSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    GlassTextField(
                        value = otpText,
                        onValueChange = { if (it.length <= 6) otpText = it.filter { char -> char.isDigit() } },
                        label = "OTP Code",
                        placeholder = "123456",
                        leadingIcon = Icons.Default.Lock,
                        errorMessage = errorMessage,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword,
                            imeAction = ImeAction.Done
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(text = "Cancel", color = glassColors.textSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        GlassButton(
                            text = "Verify",
                            onClick = { if (otpText.length >= 4) onVerifyOtp(otpText) },
                            isLoading = isLoading,
                            modifier = Modifier.width(130.dp)
                        )
                    }
                }
            }
        }
    }
}
