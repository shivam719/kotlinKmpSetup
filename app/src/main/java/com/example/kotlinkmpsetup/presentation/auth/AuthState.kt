package com.example.kotlinkmpsetup.presentation.auth

import com.example.kotlinkmpsetup.domain.model.User

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: PRESENTATION (UI State Holders)
 * ====================================================================================
 * Immutable Data Classes representing entire Login & Signup screen UI states.
 * Pre-filled with ArsenPay test credentials (`9851937038` / `July@2026`) for testing.
 */

data class LoginUiState(
    val email: String? = null,
    val password: String? = null,
    val isPasswordVisible: Boolean = false,
    val rememberMe: Boolean = true,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isOtpRequired: Boolean = false,
    val user: User? = null,
    val isSuccess: Boolean = false,
    val generalError: String? = null,
    val isOffline: Boolean = false
)

data class SignupUiState(
    val fullName: String = "",
    val outletName: String = "",
    val mobileNo: String = "",
    val emailID: String = "",
    val address: String = "",
    val pincode: String = "",
    val nameError: String? = null,
    val outletError: String? = null,
    val mobileError: String? = null,
    val emailError: String? = null,
    val addressError: String? = null,
    val pincodeError: String? = null,
    val isLoading: Boolean = false,
    val user: User? = null,
    val isSuccess: Boolean = false,
    val generalError: String? = null,
    val isOffline: Boolean = false
)
