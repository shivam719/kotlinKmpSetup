package com.example.kotlinkmpsetup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.kotlinkmpsetup.R
import com.example.kotlinkmpsetup.data.remote.ApiEndpoints.BASE_URL
import com.example.kotlinkmpsetup.domain.usecase.ValidateMobileUseCase
import com.example.kotlinkmpsetup.presentation.auth.LoginViewModel
import com.example.kotlinkmpsetup.presentation.components.NetworkStatusBanner
import com.example.kotlinkmpsetup.ui.components.GlassButton
import com.example.kotlinkmpsetup.ui.components.GlassCard
import com.example.kotlinkmpsetup.ui.components.GlassCheckbox
import com.example.kotlinkmpsetup.ui.components.GlassOtpDialog
import com.example.kotlinkmpsetup.ui.components.GlassSnackbar
import com.example.kotlinkmpsetup.ui.components.GlassTextField
import com.example.kotlinkmpsetup.ui.components.LineLoading
import com.example.kotlinkmpsetup.ui.components.LiquidBackground
import com.example.kotlinkmpsetup.ui.components.SnackbarType
import com.example.kotlinkmpsetup.ui.components.rememberGlassToastState
import com.example.kotlinkmpsetup.ui.theme.LocalGlassColors
import com.example.kotlinkmpsetup.ui.utils.AdaptiveAuthLayout
import com.example.kotlinkmpsetup.ui.utils.AppLanguage
import com.example.kotlinkmpsetup.ui.utils.GlassLanguageToggle
import org.koin.androidx.compose.koinViewModel

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: PRESENTATION (MVVM Login Screen View)
 * ====================================================================================
 * Connects Compose UI to `LoginViewModel` injected via Koin DI.
 * Collects `StateFlow` reactively for real-time field validation & online/offline status.
 */
@Composable
fun LoginScreen(
    onNavigateToSignup: () -> Unit,
    onLoginSuccess: () -> Unit,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel()
) {
    val focusManager = LocalFocusManager.current
    val glassColors = LocalGlassColors.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showForgotPasswordDialog by rememberSaveable { mutableStateOf(false) }
    var showAppBeforeLoginPopup by rememberSaveable { mutableStateOf(true) }

    val toastController = rememberGlassToastState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            toastController.show("Welcome back, ${uiState.user?.fullName ?: ""}!", SnackbarType.Success)
            viewModel.onSuccessConsumed()
            onLoginSuccess()
        }
    }

    LaunchedEffect(uiState.generalError) {
        uiState.generalError?.let { err ->
            toastController.show(err, SnackbarType.Error)
            viewModel.onErrorConsumed()
        }
    }

    LiquidBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            val blurModifier = if (showForgotPasswordDialog || uiState.isOtpRequired) Modifier.blur(16.dp) else Modifier

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .then(blurModifier)
            ) {
                // Real-time Network Offline/Online Banner
                NetworkStatusBanner(isOffline = uiState.isOffline)

                AdaptiveAuthLayout(
                    modifier = Modifier.fillMaxWidth(),
                    applyStatusBarsPadding = !uiState.isOffline,
                    heroContent = {
                        HeroBrandingPane()
                    },
                    formContent = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(glassColors.glassSurface)
                                    .border(1.dp, glassColors.accentPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Logo",
                                    tint = glassColors.accentPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = stringResource(R.string.sign_in),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = glassColors.textPrimary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = stringResource(R.string.login_subtitle),
                                fontSize = 13.sp,
                                color = glassColors.textSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            LoginFormCard(
                                email = uiState.email.orEmpty(),
                                onEmailChange = { viewModel.onEmailChanged(it) },
                                emailError = uiState.emailError,
                                password = uiState.password.orEmpty(),
                                onPasswordChange = { viewModel.onPasswordChanged(it) },
                                passwordError = uiState.passwordError,
                                passwordVisible = uiState.isPasswordVisible,
                                onPasswordToggle = { viewModel.onPasswordVisibilityToggled() },
                                rememberMe = uiState.rememberMe,
                                onRememberMeChange = { viewModel.onRememberMeToggled(it) },
                                isLoading = uiState.isLoading,
                                onSubmit = {
                                    focusManager.clearFocus()
                                    viewModel.onLoginSubmitted()
                                },
                                onForgotPassword = { showForgotPasswordDialog = true },
                                onNavigateToSignup = onNavigateToSignup
                            )
                        }
                    }
                )
            }

            // Pre-Login Promo Dialog Popup (Only shown if image exists & loads 100% successfully)
            var isImageLoadedSuccessfully by remember { mutableStateOf(false) }

            if (showAppBeforeLoginPopup) {
                AsyncImage(
                    model = "${BASE_URL}/Image/Website/1/Popup/AppBeforeLogin.png",
                    contentDescription = null,
                    onSuccess = {
                        isImageLoadedSuccessfully = true
                    },
                    onError = {
                        showAppBeforeLoginPopup = false
                        isImageLoadedSuccessfully = false
                    },
                    modifier = Modifier
                        .size(1.dp)
                        .alpha(0f)
                )

                // Display Glass Dialog ONLY when image has loaded 100% successfully
                if (isImageLoadedSuccessfully) {
                    Dialog(onDismissRequest = { showAppBeforeLoginPopup = false }) {
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentPadding = 12.dp
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AsyncImage(
                                    model = "${BASE_URL}/Image/Website/1/Popup/AppBeforeLogin.png",
                                    contentDescription = "Announcement",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                GlassButton(
                                    text = "Close",
                                    onClick = { showAppBeforeLoginPopup = false }
                                )
                            }
                        }
                    }
                }
            }

            // Forgot Password Dialog Overlay
            if (showForgotPasswordDialog) {
                ForgotPasswordDialog(
                    onDismiss = { showForgotPasswordDialog = false },
                    onSendResetLink = { resetEmail ->
                        toastController.show("Reset link sent to $resetEmail", SnackbarType.Success)
                        showForgotPasswordDialog = false
                    }
                )
            }
            
            if (uiState.isOtpRequired) {
                GlassOtpDialog(
                    onDismiss = { viewModel.onCancelOtp() },
                    onVerifyOtp = { otp -> viewModel.onValidateOtpSubmitted(otp) },
                    isLoading = uiState.isLoading
                )
            }

            // Custom Glass Floating Toast Notification
            GlassSnackbar(
                message = toastController.state().message,
                visible = toastController.state().isVisible,
                type = toastController.state().type,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

/**
 * Reusable Login Form Card
 */
@Composable
private fun LoginFormCard(
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordError: String?,
    passwordVisible: Boolean,
    onPasswordToggle: () -> Unit,
    rememberMe: Boolean,
    onRememberMeChange: (Boolean) -> Unit,
    isLoading: Boolean,
    onSubmit: () -> Unit,
    onForgotPassword: () -> Unit,
    onNavigateToSignup: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val glassColors = LocalGlassColors.current

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = 24.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            LineLoading(
                isLoading = isLoading,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            GlassTextField(
                value = email,
                onValueChange = onEmailChange,
                label = stringResource(R.string.email_label),
                placeholder = stringResource(R.string.email_placeholder),
                leadingIcon = Icons.Default.Phone,
                errorMessage = emailError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            GlassTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = stringResource(R.string.password_label),
                placeholder = stringResource(R.string.password_placeholder),
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordToggle = onPasswordToggle,
                errorMessage = passwordError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onSubmit() }
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassCheckbox(
                    checked = rememberMe,
                    onCheckedChange = onRememberMeChange,
                    label = stringResource(R.string.remember_me)
                )

                TextButton(onClick = onForgotPassword) {
                    Text(
                        text = stringResource(R.string.forgot_password),
                        color = glassColors.accentPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            GlassButton(
                text = stringResource(R.string.sign_in),
                onClick = onSubmit,
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = glassColors.glassBorder
                )
                Text(
                    text = "  ${stringResource(R.string.or_divider)}  ",
                    color = glassColors.textSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = glassColors.glassBorder
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.dont_have_account) + " ",
                    color = glassColors.textSecondary,
                    fontSize = 14.sp
                )
                Text(
                    text = stringResource(R.string.sign_up),
                    color = glassColors.accentPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToSignup() }
                )
            }
        }
    }
}


/**
 * Hero Branding Banner
 */
@Composable
private fun HeroBrandingPane() {
    val glassColors = LocalGlassColors.current

    GlassCard(
        modifier = Modifier.fillMaxSize(),
        contentPadding = 32.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(glassColors.glassSurface)
                    .border(1.5.dp, glassColors.accentPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Hero Logo",
                    tint = glassColors.accentPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.welcome_back),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = glassColors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.login_subtitle),
                fontSize = 14.sp,
                color = glassColors.textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

/**
 * Glass Dialog for Forgot Password with Real-Time Mobile Validation
 */
@Composable
private fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    onSendResetLink: (String) -> Unit
) {
    var mobileNo by rememberSaveable { mutableStateOf("") }
    var mobileError by rememberSaveable { mutableStateOf<String?>(null) }
    val validateMobileUseCase = remember { ValidateMobileUseCase() }
    val glassColors = LocalGlassColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier
                .padding(24.dp)
                .widthIn(max = 400.dp)
                .clickable(enabled = false) {},
            contentPadding = 24.dp
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.LockReset,
                    contentDescription = "Reset Password",
                    tint = glassColors.accentPrimary,
                    modifier = Modifier.size(46.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Forgot Password",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = glassColors.textPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Enter your 10-digit registered mobile number to receive password reset OTP",
                    fontSize = 13.sp,
                    color = glassColors.textSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                GlassTextField(
                    value = mobileNo,
                    onValueChange = { input ->
                        if (input.length <= 10 && input.all { it.isDigit() }) {
                            mobileNo = input
                            mobileError = if (input.isBlank()) null else validateMobileUseCase(input).errorMessage
                        }
                    },
                    label = "Mobile Number",
                    placeholder = "Enter 10-digit mobile number",
                    leadingIcon = Icons.Default.Phone,
                    errorMessage = mobileError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(R.string.cancel), color = glassColors.textSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    GlassButton(
                        text = "Send Reset Link",
                        onClick = {
                            val result = validateMobileUseCase(mobileNo)
                            if (!result.isValid) {
                                mobileError = result.errorMessage
                            } else {
                                onSendResetLink(mobileNo)
                            }
                        },
                        modifier = Modifier.width(150.dp)
                    )
                }
            }
        }
    }
}
