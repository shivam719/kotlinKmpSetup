package com.example.kotlinkmpsetup.domain.usecase

import com.example.kotlinkmpsetup.domain.model.AuthResult
import com.example.kotlinkmpsetup.domain.model.NetworkResult
import com.example.kotlinkmpsetup.domain.model.User
import com.example.kotlinkmpsetup.domain.repository.AuthRepository
import com.example.kotlinkmpsetup.domain.repository.NetworkRepository

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DOMAIN (Signup Use Case)
 * ====================================================================================
 * Executes Signup workflow.
 */
class SignupUseCase(
    private val authRepository: AuthRepository,
    private val networkRepository: NetworkRepository
) {
    suspend operator fun invoke(
        fullName: String,
        outletName: String,
        mobileNo: String,
        emailID: String,
        address: String,
        pincode: String
    ): AuthResult<User> {
        if (!networkRepository.isConnected()) {
            return NetworkResult.NetworkOffline
        }
        return authRepository.signup(
            fullName = fullName,
            outletName = outletName,
            mobileNo = mobileNo,
            emailID = emailID,
            address = address,
            pincode = pincode
        )
    }
}
