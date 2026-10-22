package com.example.kotlinkmpsetup.domain.usecase

import com.example.kotlinkmpsetup.domain.model.ValidationResult

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DOMAIN (Use Case)
 * ====================================================================================
 * Validates Password strength in real-time.
 */
class ValidatePasswordUseCase {
    operator fun invoke(password: String): ValidationResult {
        if (password.isBlank()) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Password cannot be empty"
            )
        }
        if (password.length < 6) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Password must be at least 6 characters"
            )
        }
        return ValidationResult(isValid = true)
    }
}
