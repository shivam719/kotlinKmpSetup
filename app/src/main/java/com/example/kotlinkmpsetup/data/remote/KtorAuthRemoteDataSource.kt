package com.example.kotlinkmpsetup.data.remote

import com.example.kotlinkmpsetup.data.local.DeviceInfoProvider
import com.example.kotlinkmpsetup.data.remote.dto.ArsenLoginRequestDto
import com.example.kotlinkmpsetup.data.remote.dto.ArsenLoginResponseDto
import com.example.kotlinkmpsetup.data.remote.dto.ArsenSignUpRequestDto
import com.example.kotlinkmpsetup.data.remote.dto.ArsenSignUpResponseDto
import com.example.kotlinkmpsetup.data.remote.dto.ArsenSignUpWrapperDto
import com.example.kotlinkmpsetup.domain.model.AuthResult
import com.example.kotlinkmpsetup.domain.model.AuthToken
import com.example.kotlinkmpsetup.domain.model.NetworkResult
import com.example.kotlinkmpsetup.domain.model.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DATA (Remote API Data Source)
 * ====================================================================================
 * Performs raw HTTP Network API Calls using Ktor Client wrapped in `safeApiCall`
 * targeting ArsenPay API (`https://arsenpay.com/App/Login` & `/App/AppUserSignup`).
 * Handles status code 1 (Success) and status code 2 / OTP Required.
 */
class KtorAuthRemoteDataSource(
    private val client: HttpClient,
    private val deviceInfoProvider: DeviceInfoProvider
) {

    suspend fun login(email: String, password: String): AuthResult<User> {
        val requestPayload = ArsenLoginRequestDto(
            latitude = deviceInfoProvider.latitude,
            longitude = deviceInfoProvider.longitude,
            appId = ApiEndpoints.APP_ID,
            domain = ApiEndpoints.APP_DOMAIN,
            imei = deviceInfoProvider.getDeviceImei(),
            loginTypeID = 1,
            password = password,
            regKey = "",
            serialNo = deviceInfoProvider.getDeviceSerialNo(),
            userID = email,
            version = "16"
        )

        val result = safeApiCall<ArsenLoginResponseDto> {
            client.post(ApiEndpoints.Auth.LOGIN) {
                setBody(requestPayload)
            }.body()
        }

        return when (result) {
            is NetworkResult.Success -> {
                val response = result.data
                if (response.statusCode == 1) {
                    NetworkResult.Success(response.toDomain())
                } else if (response.statusCode == 2 || response.isOTPRequired == true) {
                    NetworkResult.Error(
                        message = response.message ?: "OTP Verification Required",
                        code = 2
                    )
                } else {
                    NetworkResult.Error(message = response.message ?: "Login failed. Please check credentials.")
                }
            }
            is NetworkResult.Error -> result
            is NetworkResult.NetworkOffline -> NetworkResult.NetworkOffline
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.Pending -> NetworkResult.Pending
        }
    }

    suspend fun validateOTP(email: String, password: String, otp: String): AuthResult<User> {
        val requestPayload = ArsenLoginRequestDto(
            latitude = deviceInfoProvider.latitude,
            longitude = deviceInfoProvider.longitude,
            appId = ApiEndpoints.APP_ID,
            domain = ApiEndpoints.APP_DOMAIN,
            imei = deviceInfoProvider.getDeviceImei(),
            loginTypeID = 1,
            password = password,
            regKey = "",
            serialNo = deviceInfoProvider.getDeviceSerialNo(),
            userID = email,
            version = "16",
            otp = otp
        )

        val result = safeApiCall<ArsenLoginResponseDto> {
            client.post(ApiEndpoints.Auth.LOGIN) {
                setBody(requestPayload)
            }.body()
        }

        return when (result) {
            is NetworkResult.Success -> {
                val response = result.data
                if (response.statusCode == 1) {
                    NetworkResult.Success(response.toDomain())
                } else {
                    NetworkResult.Error(message = response.message ?: "OTP Verification failed. Please re-enter code.")
                }
            }
            is NetworkResult.Error -> result
            is NetworkResult.NetworkOffline -> NetworkResult.NetworkOffline
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.Pending -> NetworkResult.Pending
        }
    }

    suspend fun signup(
        fullName: String,
        outletName: String,
        mobileNo: String,
        emailID: String,
        address: String,
        pincode: String
    ): AuthResult<User> {
        val signUpPayload = ArsenSignUpWrapperDto(
            userCreate = ArsenSignUpRequestDto(
                name = fullName,
                outletName = outletName,
                mobileNo = mobileNo,
                emailID = emailID,
                address = address,
                pincode = pincode
            )
        )

        val result = safeApiCall<ArsenSignUpResponseDto> {
            client.post(ApiEndpoints.Auth.SIGNUP) {
                setBody(signUpPayload)
            }.body()
        }

        return when (result) {
            is NetworkResult.Success -> {
                val response = result.data
                if (response.statusCode == 1) {
                    val user = User(
                        id = response.userId ?: "usr_${response.userName ?: "new"}",
                        fullName = fullName,
                        email = emailID,
                        token = AuthToken(
                            accessToken = "signup_success_token",
                            refreshToken = response.password ?: "signup_pass"
                        )
                    )
                    NetworkResult.Success(user)
                } else {
                    NetworkResult.Error(message = response.message ?: "Signup failed. Please check details.")
                }
            }
            is NetworkResult.Error -> result
            is NetworkResult.NetworkOffline -> NetworkResult.NetworkOffline
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.Pending -> NetworkResult.Pending
        }
    }
}
