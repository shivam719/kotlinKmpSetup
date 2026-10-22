package com.example.kotlinkmpsetup.ui.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinkmpsetup.ui.theme.LocalGlassColors
import java.util.Locale

/**
 * Supported Languages: English ("en") and Hindi ("hi")
 */
enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    HINDI("hi", "हिंदी")
}

/**
 * Creates localized context for runtime language switching without restarting activity.
 */
fun Context.localizedContext(languageCode: String): Context {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    return createConfigurationContext(config)
}

/**
 * Language Provider wrapper to inject localized Context down the Composition tree.
 * Preserves [LocalActivityResultRegistryOwner] to prevent [rememberLauncherForActivityResult] crashes.
 */
@Composable
fun AppLanguageProvider(
    currentLanguage: AppLanguage,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val localizedCtx = remember(context, currentLanguage) {
        context.localizedContext(currentLanguage.code)
    }

    val activityResultOwner = remember(context) {
        context as? ActivityResultRegistryOwner
            ?: (context as? ContextWrapper)?.baseContext as? ActivityResultRegistryOwner
    }

    if (activityResultOwner != null) {
        CompositionLocalProvider(
            LocalContext provides localizedCtx,
            LocalActivityResultRegistryOwner provides activityResultOwner,
            content = content
        )
    } else {
        CompositionLocalProvider(
            LocalContext provides localizedCtx,
            content = content
        )
    }
}

/**
 * Compact Glass Language Switcher Button (EN / हिंदी Toggle)
 */
@Composable
fun GlassLanguageToggle(
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current

    val nextLanguage = if (currentLanguage == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(glassColors.glassSurface)
            .border(1.dp, glassColors.glassBorder, RoundedCornerShape(12.dp))
            .clickable { onLanguageChange(nextLanguage) }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (currentLanguage == AppLanguage.ENGLISH) "हिंदी" else "EN",
            color = glassColors.textPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
