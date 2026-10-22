package com.example.kotlinkmpsetup.domain.usecase

import com.example.kotlinkmpsetup.domain.model.AuthResult
import com.example.kotlinkmpsetup.domain.model.NetworkResult
import com.example.kotlinkmpsetup.domain.model.User
import com.example.kotlinkmpsetup.domain.repository.AuthRepository
import com.example.kotlinkmpsetup.domain.repository.NetworkRepository

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DOMAIN (Login Use Case)
 * ====================================================================================
 * Executes Login workflow after validating network connectivity & input sanity.
 *
 * INTERVIEW POINT:
 * Use Cases orchestrate repositories. `LoginUseCase` first checks `NetworkRepository`
 * to verify if device is online before calling `AuthRepository.login()`. This prevents
 * unnecessary network calls when offline.
 */
class LoginUseCase(
    private val authRepository: AuthRepository,
    private val networkRepository: NetworkRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthResult<User> {
        if (!networkRepository.isConnected()) {
            return NetworkResult.NetworkOffline
        }
        return authRepository.login(email = email, password = password)
    }
}
