package com.example.kotlinkmpsetup.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinkmpsetup.data.local.DeviceInfoProvider
import com.example.kotlinkmpsetup.domain.model.NetworkStatus
import com.example.kotlinkmpsetup.domain.model.fold
import com.example.kotlinkmpsetup.domain.repository.AuthRepository
import com.example.kotlinkmpsetup.domain.repository.NetworkRepository
import com.example.kotlinkmpsetup.domain.usecase.LoginUseCase
import com.example.kotlinkmpsetup.domain.usecase.ValidateMobileUseCase
import com.example.kotlinkmpsetup.domain.usecase.ValidatePasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: PRESENTATION (MVVM Login ViewModel)
 * ====================================================================================
 * Manages Login StateFlow, User Input Validation, OTP Validation, and Network Monitoring.
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val networkRepository: NetworkRepository,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val authRepository: AuthRepository,
    private val validateMobileUseCase: ValidateMobileUseCase = ValidateMobileUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        observeNetworkConnectivity()
    }

    private fun observeNetworkConnectivity() {
        viewModelScope.launch {
            networkRepository.observeNetworkStatus().collect { status ->
                _uiState.update { currentState ->
                    currentState.copy(isOffline = status == NetworkStatus.Lost || status == NetworkStatus.Unavailable)
                }
            }
        }
    }

    // Clears error state after one-time UI consumption to prevent duplicate toasts on theme toggle/recomposition
    fun onErrorConsumed() {
        _uiState.update { it.copy(generalError = null) }
    }

    // Clears success state & password field after one-time UI consumption for security
    fun onSuccessConsumed() {
        _uiState.update { it.copy(isSuccess = false, password = "", passwordError = null) }
    }

    fun onCancelOtp() {
        _uiState.update { it.copy(isOtpRequired = false) }
    }

    // Dynamic User Input Change & Real-Time Validation
    fun onEmailChanged(input: String) {
        val validationResult = validateMobileUseCase(input)
        _uiState.update {
            it.copy(
                email = input,
                emailError = if (input.isBlank()) null else validationResult.errorMessage
            )
        }
    }

    // Real-Time Password Input Change & Validation
    fun onPasswordChanged(password: String) {
        val validationResult = validatePasswordUseCase(password)
        _uiState.update {
            it.copy(
                password = password,
                passwordError = if (password.isBlank()) null else validationResult.errorMessage
            )
        }
    }

    fun onPasswordVisibilityToggled() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onRememberMeToggled(remember: Boolean) {
        _uiState.update { it.copy(rememberMe = remember) }
    }

    fun onLoginSubmitted() {
        val userInput = _uiState.value.email.orEmpty()
        val userPassword = _uiState.value.password.orEmpty()

        val mobileResult = validateMobileUseCase(userInput)
        val passwordResult = validatePasswordUseCase(userPassword)

        if (!mobileResult.isValid || !passwordResult.isValid) {
            _uiState.update {
                it.copy(
                    emailError = mobileResult.errorMessage,
                    passwordError = passwordResult.errorMessage
                )
            }
            return
        }

        // Prompt GPS settings enabler if location is turned off
        if (!deviceInfoProvider.isGpsEnabled() && deviceInfoProvider.isLocationPermissionGranted()) {
            deviceInfoProvider.promptEnableGpsSettings()
        }

        // Refresh live GPS location before submitting API request
        deviceInfoProvider.fetchRealTimeLocation()

        _uiState.update { it.copy(isLoading = true, generalError = null) }

        viewModelScope.launch {
            loginUseCase(userInput, userPassword).fold(
                onPending = { _uiState.update { it.copy(isLoading = false) } },
                onLoading = { _uiState.update { it.copy(isLoading = true) } },
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            user = user,
                            generalError = null
                        )
                    }
                },
                onError = { message, code ->
                    if (code == 2) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isOtpRequired = true,
                                otpSession = message,
                                generalError = null
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                generalError = message
                            )
                        }
                    }
                },
                onOffline = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isOffline = true,
                            generalError = "Device is offline. Please check your internet connection."
                        )
                    }
                }
            )
        }
    }

    fun onValidateOtpSubmitted(otp: String) {
        val userInput = _uiState.value.email.orEmpty()
        val userPassword = _uiState.value.password.orEmpty()
        val session = _uiState.value.otpSession

        _uiState.update { it.copy(isLoading = true, generalError = null) }

        viewModelScope.launch {
            authRepository.validateOTP(userInput, userPassword, otp, session).fold(
                onPending = { _uiState.update { it.copy(isLoading = false) } },
                onLoading = { _uiState.update { it.copy(isLoading = true) } },
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isOtpRequired = false,
                            isSuccess = true,
                            user = user,
                            generalError = null
                        )
                    }
                },
                onError = { message, _ ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            generalError = message
                        )
                    }
                },
                onOffline = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isOffline = true,
                            generalError = "Device is offline. Please check your internet connection."
                        )
                    }
                }
            )
        }
    }
}
