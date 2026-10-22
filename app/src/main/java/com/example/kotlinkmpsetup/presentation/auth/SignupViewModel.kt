package com.example.kotlinkmpsetup.presentation.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinkmpsetup.domain.model.NetworkStatus
import com.example.kotlinkmpsetup.domain.model.fold
import com.example.kotlinkmpsetup.domain.repository.NetworkRepository
import com.example.kotlinkmpsetup.domain.usecase.SignupUseCase
import com.example.kotlinkmpsetup.domain.usecase.ValidateMobileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: PRESENTATION (MVVM Signup ViewModel)
 * ====================================================================================
 * Manages Signup StateFlow & Real-Time Validation for Name, Outlet, Mobile, Email, Address, Pincode.
 */
class SignupViewModel(
    private val signupUseCase: SignupUseCase,
    private val networkRepository: NetworkRepository,
    private val validateMobileUseCase: ValidateMobileUseCase = ValidateMobileUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

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

    fun onErrorConsumed() {
        _uiState.update { it.copy(generalError = null) }
    }

    fun onSuccessConsumed() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    fun onFullNameChanged(name: String) {
        _uiState.update {
            it.copy(
                fullName = name,
                nameError = if (name.isBlank() || name.length >= 3) null else "Enter valid name (min 3 chars)"
            )
        }
    }

    fun onOutletNameChanged(outlet: String) {
        _uiState.update {
            it.copy(
                outletName = outlet,
                outletError = if (outlet.isBlank() || outlet.length >= 3) null else "Enter valid outlet name (min 3 chars)"
            )
        }
    }

    fun onMobileNoChanged(input: String) {
        val sanitized = ValidateMobileUseCase.sanitizeMobileNumber(input)
        val validationResult = validateMobileUseCase(sanitized)
        _uiState.update {
            it.copy(
                mobileNo = sanitized,
                mobileError = if (sanitized.isBlank()) null else validationResult.errorMessage
            )
        }
    }

    fun onEmailIDChanged(email: String) {
        val isEmailValid = email.isBlank() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
        _uiState.update {
            it.copy(
                emailID = email,
                emailError = if (isEmailValid) null else "Enter valid email address"
            )
        }
    }

    fun onAddressChanged(addr: String) {
        _uiState.update {
            it.copy(
                address = addr,
                addressError = if (addr.isNotBlank()) null else "Enter address"
            )
        }
    }

    fun onPincodeChanged(pin: String) {
        val digitsOnly = pin.filter { char -> char.isDigit() }.take(6)
        _uiState.update {
            it.copy(
                pincode = digitsOnly,
                pincodeError = if (digitsOnly.length == 6) null else "Enter valid 6-digit pin code"
            )
        }
    }

    fun onSignupSubmitted() {
        val state = _uiState.value
        val sanitizedMobile = ValidateMobileUseCase.sanitizeMobileNumber(state.mobileNo)
        val mobileResult = validateMobileUseCase(sanitizedMobile)

        val nameValid = state.fullName.trim().length >= 3
        val outletValid = state.outletName.trim().length >= 3
        val mobileValid = mobileResult.isValid
        val emailValid = state.emailID.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(state.emailID).matches()
        val addressValid = state.address.isNotBlank()
        val pincodeValid = state.pincode.length == 6

        if (!nameValid || !outletValid || !mobileValid || !emailValid || !addressValid || !pincodeValid) {
            _uiState.update {
                it.copy(
                    mobileNo = sanitizedMobile,
                    nameError = if (nameValid) null else "Enter valid name (min 3 chars)",
                    outletError = if (outletValid) null else "Enter valid outlet name (min 3 chars)",
                    mobileError = mobileResult.errorMessage,
                    emailError = if (emailValid) null else "Enter valid email address",
                    addressError = if (addressValid) null else "Enter address",
                    pincodeError = if (pincodeValid) null else "Enter valid 6-digit pin code"
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, generalError = null) }

        viewModelScope.launch {
            signupUseCase(
                fullName = state.fullName.trim(),
                outletName = state.outletName.trim(),
                mobileNo = sanitizedMobile,
                emailID = state.emailID.trim(),
                address = state.address.trim(),
                pincode = state.pincode.trim()
            ).fold(
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
