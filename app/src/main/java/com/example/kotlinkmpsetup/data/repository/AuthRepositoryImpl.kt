package com.example.kotlinkmpsetup.data.repository

import com.example.kotlinkmpsetup.data.local.TokenDataStoreManager
import com.example.kotlinkmpsetup.data.remote.KtorAuthRemoteDataSource
import com.example.kotlinkmpsetup.domain.model.AuthResult
import com.example.kotlinkmpsetup.domain.model.User
import com.example.kotlinkmpsetup.domain.model.onSuccess
import com.example.kotlinkmpsetup.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DATA (Repository Implementation)
 * ====================================================================================
 * Implements `AuthRepository` domain interface. Combines `KtorAuthRemoteDataSource`
 * and `TokenDataStoreManager`.
 */
class AuthRepositoryImpl(
    private val remoteDataSource: KtorAuthRemoteDataSource,
    private val tokenManager: TokenDataStoreManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthResult<User> {
        val result = remoteDataSource.login(email = email, password = password)
        result.onSuccess { user ->
            tokenManager.saveUserSession(user)
        }
        return result
    }

    override suspend fun validateOTP(email: String, password: String, otp: String): AuthResult<User> {
        val result = remoteDataSource.validateOTP(email = email, password = password, otp = otp)
        result.onSuccess { user ->
            tokenManager.saveUserSession(user)
        }
        return result
    }

    override suspend fun signup(
        fullName: String,
        outletName: String,
        mobileNo: String,
        emailID: String,
        address: String,
        pincode: String
    ): AuthResult<User> {
        val result = remoteDataSource.signup(
            fullName = fullName,
            outletName = outletName,
            mobileNo = mobileNo,
            emailID = emailID,
            address = address,
            pincode = pincode
        )
        result.onSuccess { user ->
            tokenManager.saveUserSession(user)
        }
        return result
    }

    override suspend fun saveAuthToken(accessToken: String, refreshToken: String) {
        tokenManager.saveTokens(accessToken, refreshToken)
    }

    override fun getSavedAccessToken(): Flow<String?> {
        return tokenManager.accessTokenFlow
    }

    override suspend fun logout() {
        tokenManager.clearTokens()
    }
}
