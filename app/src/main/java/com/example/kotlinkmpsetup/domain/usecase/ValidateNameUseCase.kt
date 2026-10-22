package com.example.kotlinkmpsetup.domain.usecase

import com.example.kotlinkmpsetup.domain.model.ValidationResult

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DOMAIN (Use Case)
 * ====================================================================================
 * Validates Full Name input.
 */
class ValidateNameUseCase {
    operator fun invoke(fullName: String): ValidationResult {
        if (fullName.isBlank()) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Full name cannot be empty"
            )
        }
        if (fullName.trim().length < 2) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Full name must be at least 2 characters"
            )
        }
        return ValidationResult(isValid = true)
    }
}
