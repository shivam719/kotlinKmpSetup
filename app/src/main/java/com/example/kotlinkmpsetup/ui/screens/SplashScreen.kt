package com.example.kotlinkmpsetup.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinkmpsetup.R
import com.example.kotlinkmpsetup.data.local.DeviceInfoProvider
import com.example.kotlinkmpsetup.ui.components.GlassButton
import com.example.kotlinkmpsetup.ui.components.GlassCard
import com.example.kotlinkmpsetup.ui.components.LiquidBackground
import com.example.kotlinkmpsetup.ui.theme.LocalGlassColors
import com.example.kotlinkmpsetup.ui.utils.AppLanguage
import com.example.kotlinkmpsetup.ui.utils.GlassLanguageToggle
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.milliseconds

/**
 * Animated Liquid Glass Splash Screen with Runtime Location & Notification Permissions.
 */
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier,
    deviceInfoProvider: DeviceInfoProvider = koinInject()
) {
    val glassColors = LocalGlassColors.current
    var startAnimation by rememberSaveable { mutableStateOf(false) }

    // Multi-Permission Launcher for Location & Notifications
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Fetch GPS Location after permission dialog closes
        deviceInfoProvider.fetchRealTimeLocation()
    }

    LaunchedEffect(Unit) {
        startAnimation = true

        // Prepare Permissions List
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Launch Permissions Request
        permissionsLauncher.launch(permissions.toTypedArray())

        delay(3200.milliseconds)
        onNavigateToLogin()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val contentScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.7f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "contentScale"
    )

    LiquidBackground(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp)
        ) {
            // Language Switcher Top Right
            GlassLanguageToggle(
                currentLanguage = currentLanguage,
                onLanguageChange = onLanguageChange,
                modifier = Modifier.align(Alignment.TopEnd)
            )

            AnimatedVisibility(
                visible = startAnimation,
                enter = fadeIn(animationSpec = tween(1000)) + scaleIn(
                    initialScale = 0.8f,
                    animationSpec = tween(1000)
                ),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(contentScale),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.size(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Outer Pulsing Wave
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .graphicsLayer {
                                    scaleX = pulseScale
                                    scaleY = pulseScale
                                }
                                .clip(CircleShape)
                                .drawBehind {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                glassColors.accentPrimary.copy(alpha = pulseAlpha),
                                                glassColors.accentSecondary.copy(alpha = pulseAlpha * 0.5f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                }
                        )

                        // Central Logo Sphere
                        Box(
                            modifier = Modifier
                                .size(105.dp)
                                .clip(CircleShape)
                                .background(glassColors.glassSurface)
                                .border(
                                    width = 1.5.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            glassColors.glassBorder,
                                            glassColors.accentPrimary,
                                            glassColors.accentTertiary
                                        )
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "App Logo",
                                tint = glassColors.accentPrimary,
                                modifier = Modifier.size(52.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        contentPadding = 28.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(R.string.app_name),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = glassColors.textPrimary,
                                letterSpacing = 2.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(R.string.app_tagline),
                                fontSize = 14.sp,
                                color = glassColors.textSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            GlassButton(
                                text = stringResource(R.string.get_started),
                                onClick = onNavigateToLogin,
                                icon = Icons.AutoMirrored.Filled.ArrowForward
                            )
                        }
                    }
                }
            }
        }
    }
}
