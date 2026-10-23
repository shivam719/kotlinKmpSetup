package com.example.kotlinkmpsetup.domain.repository

import com.example.kotlinkmpsetup.domain.model.AuthResult
import com.example.kotlinkmpsetup.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DOMAIN (Auth Repository Contract)
 * ====================================================================================
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult<User>
    suspend fun validateOTP(email: String, password: String, otp: String, otpSession: String? = null): AuthResult<User>
    suspend fun signup(
        fullName: String,
        outletName: String,
        mobileNo: String,
        emailID: String,
        address: String,
        pincode: String
    ): AuthResult<User>

    suspend fun saveAuthToken(accessToken: String, refreshToken: String)
    fun getSavedAccessToken(): Flow<String?>
    suspend fun logout()
}
