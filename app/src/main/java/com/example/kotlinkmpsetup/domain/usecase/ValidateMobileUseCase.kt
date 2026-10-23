package com.example.kotlinkmpsetup.domain.usecase

import com.example.kotlinkmpsetup.domain.model.ValidationResult

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DOMAIN (Use Case - Mobile / User Input Validation)
 * ====================================================================================
 * Validates Mobile Number / Email input format dynamically based on selected language.
 */
class ValidateMobileUseCase {

    operator fun invoke(input: String, isHindi: Boolean = false): ValidationResult {
        val trimmed = input.trim()

        if (trimmed.isBlank()) {
            return ValidationResult(
                isValid = false,
                errorMessage = if (isHindi) "मोबाइल नंबर दर्ज करें" else "Please enter mobile number"
            )
        }

        if (trimmed.length < 10) {
            return ValidationResult(
                isValid = false,
                errorMessage = if (isHindi) "10 अंकों का वैध मोबाइल नंबर दर्ज करें" else "Please enter valid 10-digit mobile number"
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
