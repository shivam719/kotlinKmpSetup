package com.example.kotlinkmpsetup.domain.usecase

import com.example.kotlinkmpsetup.domain.model.ValidationResult

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DOMAIN (Use Case - Mobile / User Input Validation)
 * ====================================================================================
 * Validates Mobile Number / Email input format dynamically based on user entry.
 */
class ValidateMobileUseCase {

    operator fun invoke(input: String): ValidationResult {
        val trimmed = input.trim()

        if (trimmed.isBlank()) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Mobile number / Email cannot be empty"
            )
        }

        if (trimmed.length < 10) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Please enter valid 10-digit mobile number or email"
            )
        }

        return ValidationResult(isValid = true)
    }

    companion object {
        fun sanitizeMobileNumber(input: String): String {
            return input.trim()
        }
    }
}
