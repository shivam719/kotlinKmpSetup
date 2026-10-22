package com.example.kotlinkmpsetup.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kotlinkmpsetup.R
import com.example.kotlinkmpsetup.presentation.auth.SignupViewModel
import com.example.kotlinkmpsetup.presentation.components.NetworkStatusBanner
import com.example.kotlinkmpsetup.ui.components.DialogStatusType
import com.example.kotlinkmpsetup.ui.components.GlassButton
import com.example.kotlinkmpsetup.ui.components.GlassCard
import com.example.kotlinkmpsetup.ui.components.GlassIconButton
import com.example.kotlinkmpsetup.ui.components.GlassSnackbar
import com.example.kotlinkmpsetup.ui.components.GlassStatusDialog
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
 * CLEAN ARCHITECTURE LAYER: PRESENTATION (MVVM Signup View)
 * ====================================================================================
 * Connects Compose UI to `SignupViewModel` with real-time field validation & network status.
 */
@Composable
fun SignupScreen(
    onNavigateToLogin: () -> Unit,
    onSignupSuccess: () -> Unit,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignupViewModel = koinViewModel()
) {
    val focusManager = LocalFocusManager.current
    val glassColors = LocalGlassColors.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toastController = rememberGlassToastState()
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }

    // Intercept System Back Gesture / Button to return to Login
    BackHandler {
        onNavigateToLogin()
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            showSuccessDialog = true
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
            val blurModifier = if (showSuccessDialog) Modifier.blur(16.dp) else Modifier

            Column(modifier = Modifier.fillMaxSize().then(blurModifier)) {
                NetworkStatusBanner(isOffline = uiState.isOffline)

                AdaptiveAuthLayout(
                    modifier = Modifier.weight(1f),
                    applyStatusBarsPadding = !uiState.isOffline,
                    topEndAction = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GlassIconButton(
                                onClick = onNavigateToLogin,
                                modifier = Modifier.size(36.dp),
                                iconContent = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back to Login",
                                        tint = glassColors.textPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            GlassLanguageToggle(
                                currentLanguage = currentLanguage,
                                onLanguageChange = onLanguageChange
                            )
                        }
                    },
                    heroContent = {
                        HeroSignupBrandingPane()
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
                                text = "Create a New Account",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = glassColors.textPrimary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = stringResource(R.string.signup_subtitle),
                                fontSize = 13.sp,
                                color = glassColors.textSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            SignupFormCard(
                                fullName = uiState.fullName,
                                onFullNameChange = { viewModel.onFullNameChanged(it) },
                                nameError = uiState.nameError,
                                outletName = uiState.outletName,
                                onOutletNameChange = { viewModel.onOutletNameChanged(it) },
                                outletError = uiState.outletError,
                                mobileNo = uiState.mobileNo,
                                onMobileNoChange = { viewModel.onMobileNoChanged(it) },
                                mobileError = uiState.mobileError,
                                emailID = uiState.emailID,
                                onEmailIDChange = { viewModel.onEmailIDChanged(it) },
                                emailError = uiState.emailError,
                                address = uiState.address,
                                onAddressChange = { viewModel.onAddressChanged(it) },
                                addressError = uiState.addressError,
                                pincode = uiState.pincode,
                                onPincodeChange = { viewModel.onPincodeChanged(it) },
                                pincodeError = uiState.pincodeError,
                                isLoading = uiState.isLoading,
                                onSubmit = {
                                    focusManager.clearFocus()
                                    viewModel.onSignupSubmitted()
                                },
                                onNavigateToLogin = onNavigateToLogin
                            )
                        }
                    }
                )
            }

            // Success Dialog Overlay
            if (showSuccessDialog) {
                GlassStatusDialog(
                    statusType = DialogStatusType.Success,
                    title = "Registration Successful",
                    message = "Welcome! Your account has been created successfully.",
                    confirmText = "Proceed to Login",
                    onDismiss = {
                        showSuccessDialog = false
                        viewModel.onSuccessConsumed()
                        onSignupSuccess()
                    }
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
 * Reusable Glass Form Card for Signup
 */
@Composable
private fun SignupFormCard(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    nameError: String?,
    outletName: String,
    onOutletNameChange: (String) -> Unit,
    outletError: String?,
    mobileNo: String,
    onMobileNoChange: (String) -> Unit,
    mobileError: String?,
    emailID: String,
    onEmailIDChange: (String) -> Unit,
    emailError: String?,
    address: String,
    onAddressChange: (String) -> Unit,
    addressError: String?,
    pincode: String,
    onPincodeChange: (String) -> Unit,
    pincodeError: String?,
    isLoading: Boolean,
    onSubmit: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val glassColors = LocalGlassColors.current

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = 24.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            LineLoading(
                isLoading = isLoading,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Full Name
            GlassTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = "Full Name",
                placeholder = "Full Name",
                leadingIcon = Icons.Default.Person,
                errorMessage = nameError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Outlet Name
            GlassTextField(
                value = outletName,
                onValueChange = onOutletNameChange,
                label = "Outlet Name",
                placeholder = "Outlet / Shop Name",
                leadingIcon = Icons.Default.Home,
                errorMessage = outletError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mobile Number
            GlassTextField(
                value = mobileNo,
                onValueChange = onMobileNoChange,
                label = "Mobile Number",
                placeholder = "9851937038",
                leadingIcon = Icons.Default.Phone,
                errorMessage = mobileError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Email Address
            GlassTextField(
                value = emailID,
                onValueChange = onEmailIDChange,
                label = "Email Address",
                placeholder = "retailer@domain.com",
                leadingIcon = Icons.Default.Email,
                errorMessage = emailError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Address
            GlassTextField(
                value = address,
                onValueChange = onAddressChange,
                label = "Address",
                placeholder = "Shop Address",
                leadingIcon = Icons.Default.LocationOn,
                errorMessage = addressError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Pin Code
            GlassTextField(
                value = pincode,
                onValueChange = onPincodeChange,
                label = "Pin Code",
                placeholder = "713101",
                leadingIcon = Icons.Default.Lock,
                errorMessage = pincodeError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onSubmit() }
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            GlassButton(
                text = "Register Free",
                onClick = onSubmit,
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = glassColors.glassBorder
                )
                Text(
                    text = "  ${stringResource(R.string.or_sign_up_with)}  ",
                    color = glassColors.textSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = glassColors.glassBorder
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.already_have_account) + " ",
                    color = glassColors.textSecondary,
                    fontSize = 14.sp
                )
                Text(
                    text = stringResource(R.string.sign_in),
                    color = glassColors.accentPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

/**
 * Side Pane Hero Branding for Signup Screen
 */
@Composable
private fun HeroSignupBrandingPane() {
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
                    contentDescription = "Signup Hero",
                    tint = glassColors.accentPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Create Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = glassColors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.signup_subtitle),
                fontSize = 14.sp,
                color = glassColors.textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}
