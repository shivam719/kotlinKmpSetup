package com.example.kotlinkmpsetup.ui.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Screen Form Factors for Device Responsiveness.
 */
enum class DeviceFormFactor {
    CompactMobilePortrait,
    CompactMobileLandscape,
    ExpandedTablet
}

/**
 * Single Centralized Adaptive Layout Engine.
 * Manages Mobile Portrait, Mobile Landscape, Tablet, Foldable, and Desktop displays.
 */
@Composable
fun AdaptiveAuthLayout(
    modifier: Modifier = Modifier,
    applyStatusBarsPadding: Boolean = true,
    topEndAction: (@Composable () -> Unit)? = null,
    heroContent: @Composable () -> Unit,
    formContent: @Composable () -> Unit
) {
    val statusPaddingModifier = if (applyStatusBarsPadding) Modifier.statusBarsPadding() else Modifier

    Box(modifier = modifier.fillMaxSize()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .then(statusPaddingModifier)
                .navigationBarsPadding()
                .imePadding()
        ) {
            val isWide = maxWidth >= 650.dp
            val isLandscape = maxWidth > maxHeight && maxHeight < 500.dp

            val formFactor = when {
                isWide -> DeviceFormFactor.ExpandedTablet
                isLandscape -> DeviceFormFactor.CompactMobileLandscape
                else -> DeviceFormFactor.CompactMobilePortrait
            }

            if (formFactor == DeviceFormFactor.ExpandedTablet || formFactor == DeviceFormFactor.CompactMobileLandscape) {
                // Multi-Pane Side-By-Side Layout (Tablet / Landscape)
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 28.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Left Hero Pane
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(end = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        heroContent()
                    }

                    // Right Form Pane
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .widthIn(max = 460.dp)
                        ) {
                            formContent()
                        }
                    }
                }
            } else {
                // Mobile Portrait Single Column Layout
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 420.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(28.dp))
                        formContent()
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }

        // Top Action Slot (e.g. Language Switcher)
        if (topEndAction != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .then(statusPaddingModifier)
                    .padding(top = 12.dp, end = 20.dp)
            ) {
                topEndAction()
            }
        }
    }
}
