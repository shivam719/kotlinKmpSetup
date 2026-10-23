package com.example.kotlinkmpsetup.domain.usecase

import com.example.kotlinkmpsetup.domain.model.ValidationResult

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DOMAIN (Use Case - Password Validation)
 * ====================================================================================
 * Validates Password strength in real-time dynamically based on selected language.
 */
class ValidatePasswordUseCase {
    operator fun invoke(password: String, isHindi: Boolean = false): ValidationResult {
        if (password.isBlank()) {
            return ValidationResult(
                isValid = false,
                errorMessage = if (isHindi) "पासवर्ड दर्ज करें" else "Please enter password"
            )
        }
        if (password.length < 6) {
            return ValidationResult(
                isValid = false,
                errorMessage = if (isHindi) "पासवर्ड कम से कम 6 अक्षरों का होना चाहिए" else "Password must be at least 6 characters"
            )
        }
        return ValidationResult(isValid = true)
    }
}
