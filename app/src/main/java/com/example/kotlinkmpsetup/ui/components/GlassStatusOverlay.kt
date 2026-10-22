package com.example.kotlinkmpsetup.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinkmpsetup.domain.model.NetworkResult
import com.example.kotlinkmpsetup.ui.theme.LocalGlassColors

/**
 * ====================================================================================
 * REUSABLE UI: UNIVERSAL GENERIC NETWORK STATUS OVERLAY & CONTENT WRAPPER
 * ====================================================================================
 * A generic composable wrapper that automatically pattern-matches [NetworkResult] states:
 * - [NetworkResult.Pending]: Displays idle placeholder.
 * - [NetworkResult.Loading]: Renders animated [LineLoading] + circular spinner.
 * - [NetworkResult.Success]: Executes content lambda with payload [T].
 * - [NetworkResult.Error]: Renders glass error card with Retry button.
 * - [NetworkResult.NetworkOffline]: Renders offline state card with Reconnect button.
 *
 * INTERVIEW POINTS COVERED:
 * 1. **Generic Composables (`<T>`)**: Accepts generic state payload [NetworkResult].
 * 2. **Animated Content State Transitions**: Smooth cross-fade between loading, error, & content states.
 */
@Composable
fun <T> GlassContentWrapper(
    result: NetworkResult<T>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (data: T) -> Unit
) {
    val glassColors = LocalGlassColors.current

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = result,
            transitionSpec = { fadeIn().togetherWith(fadeOut()) },
            label = "ContentStatusTransition",
            modifier = Modifier.fillMaxSize()
        ) { state ->
            when (state) {
                is NetworkResult.Pending -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ready to load data...",
                            color = glassColors.textSecondary,
                            fontSize = 14.sp
                        )
                    }
                }

                is NetworkResult.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            GlassListShimmer(itemCount = 3)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading data, please wait...",
                                color = glassColors.textSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                is NetworkResult.Success -> {
                    content(state.data)
                }

                is NetworkResult.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 400.dp),
                            contentPadding = 24.dp
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Error",
                                    tint = glassColors.accentTertiary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Operation Failed",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = glassColors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = state.message,
                                    fontSize = 13.sp,
                                    color = glassColors.textSecondary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                GlassButton(
                                    text = "Retry",
                                    onClick = onRetry,
                                    icon = Icons.Default.Refresh
                                )
                            }
                        }
                    }
                }

                is NetworkResult.NetworkOffline -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 400.dp),
                            contentPadding = 24.dp
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.WifiOff,
                                    contentDescription = "Offline",
                                    tint = glassColors.accentSecondary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "No Internet Connection",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = glassColors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Please check your network settings and try again.",
                                    fontSize = 13.sp,
                                    color = glassColors.textSecondary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                GlassButton(
                                    text = "Try Reconnecting",
                                    onClick = onRetry,
                                    icon = Icons.Default.Refresh
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
