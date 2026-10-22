package com.example.kotlinkmpsetup.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinkmpsetup.R
import com.example.kotlinkmpsetup.data.local.TokenDataStoreManager
import com.example.kotlinkmpsetup.ui.components.GlassButton
import com.example.kotlinkmpsetup.ui.components.GlassCard
import com.example.kotlinkmpsetup.ui.components.GlassSnackbar
import com.example.kotlinkmpsetup.ui.components.LiquidBackground
import com.example.kotlinkmpsetup.ui.components.SnackbarType
import com.example.kotlinkmpsetup.ui.components.rememberGlassToastState
import com.example.kotlinkmpsetup.ui.theme.LiquidGlassTheme
import com.example.kotlinkmpsetup.ui.theme.LocalGlassColors
import com.example.kotlinkmpsetup.ui.utils.AppLanguage
import com.example.kotlinkmpsetup.ui.utils.AppLanguageProvider
import com.example.kotlinkmpsetup.ui.utils.GlassLanguageToggle
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

enum class AuthScreenState {
    Splash,
    Login,
    Signup,
    Home
}

/**
 * Main Authentication Navigation Controller with Liquid Glass Transitions,
 * Auto-Login Session Check, and Centralized Adaptive Layouts.
 */
@Composable
fun AuthNavigation(
    modifier: Modifier = Modifier,
    tokenManager: TokenDataStoreManager = koinInject()
) {
    var currentScreen by rememberSaveable { mutableStateOf(AuthScreenState.Splash) }
    var currentLanguage by rememberSaveable { mutableStateOf(AppLanguage.ENGLISH) }
    val darkTheme = isSystemInDarkTheme()
    val toastController = rememberGlassToastState()

    // Global Session Expiry / Invalid Session Auto-Logout Observer
    LaunchedEffect(tokenManager) {
        tokenManager.sessionExpiredEvent.collect {
            currentScreen = AuthScreenState.Login
            toastController.show("Session expired. Please sign in again.", SnackbarType.Error)
        }
    }

    AppLanguageProvider(currentLanguage = currentLanguage) {
        LiquidGlassTheme(darkTheme = darkTheme) {
            val glassColors = LocalGlassColors.current

            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(glassColors.background)
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        if (targetState.ordinal > initialState.ordinal) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut()
                            )
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut()
                            )
                        }
                    },
                    label = "AuthScreenTransition",
                    modifier = Modifier.fillMaxSize()
                ) { screen ->
                    when (screen) {
                        AuthScreenState.Splash -> {
                            SplashScreen(
                                onNavigateToLogin = {
                                    // Auto-Login Check: If token is saved, go directly to Home Dashboard
                                    currentScreen = if (tokenManager.isLoggedIn()) {
                                        AuthScreenState.Home
                                    } else {
                                        AuthScreenState.Login
                                    }
                                },
                                currentLanguage = currentLanguage,
                                onLanguageChange = { currentLanguage = it }
                            )
                        }

                        AuthScreenState.Login -> {
                            LoginScreen(
                                onNavigateToSignup = { currentScreen = AuthScreenState.Signup },
                                onLoginSuccess = { currentScreen = AuthScreenState.Home },
                                currentLanguage = currentLanguage,
                                onLanguageChange = { currentLanguage = it }
                            )
                        }

                        AuthScreenState.Signup -> {
                            SignupScreen(
                                onNavigateToLogin = { currentScreen = AuthScreenState.Login },
                                onSignupSuccess = { currentScreen = AuthScreenState.Login },
                                currentLanguage = currentLanguage,
                                onLanguageChange = { currentLanguage = it }
                            )
                        }

                        AuthScreenState.Home -> {
                            HomeScreen(
                                onLogout = { currentScreen = AuthScreenState.Login },
                                currentLanguage = currentLanguage,
                                onLanguageChange = { currentLanguage = it }
                            )
                        }
                    }
                }

                // Global Glass Floating Toast Notification
                GlassSnackbar(
                    message = toastController.state().message,
                    visible = toastController.state().isVisible,
                    type = toastController.state().type,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}
