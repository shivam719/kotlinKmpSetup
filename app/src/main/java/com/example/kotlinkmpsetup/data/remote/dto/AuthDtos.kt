package com.example.kotlinkmpsetup.data.remote.dto

import com.example.kotlinkmpsetup.domain.model.AuthToken
import com.example.kotlinkmpsetup.domain.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DATA (DTOs - Data Transfer Objects)
 * ====================================================================================
 * JSON Serialized objects matching the ArsenPay API (`https://arsenpay.com/App/Login` & `/App/AppUserSignup`).
 */

@Serializable
data class ArsenLoginRequestDto(
    @SerialName("Latitude") val latitude: Double,
    @SerialName("Longitude") val longitude: Double,
    @SerialName("appid") val appId: String,
    @SerialName("domain") val domain: String,
    @SerialName("imei") val imei: String,
    @SerialName("loginTypeID") val loginTypeID: Int,
    @SerialName("password") val password: String,
    @SerialName("regKey") val regKey: String,
    @SerialName("serialNo") val serialNo: String,
    @SerialName("userID") val userID: String,
    @SerialName("version") val version: String,
    @SerialName("otp") val otp: String? = null,
    @SerialName("oTPSession") val otpSession: String? = null
)

@Serializable
data class ArsenUserDataDto(
    @SerialName("userID") val userID: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("mobileNo") val mobileNo: String? = null,
    @SerialName("emailID") val emailID: String? = null,
    @SerialName("sessionID") val sessionID: Long? = null,
    @SerialName("session") val sessionToken: String? = null,
    @SerialName("roleName") val roleName: String? = null,
    @SerialName("roleID") val roleID: Int? = null,
    @SerialName("slabID") val slabID: Int? = null,
    @SerialName("outletID") val outletID: Int? = null,
    @SerialName("outletName") val outletName: String? = null,
    @SerialName("state") val state: String? = null,
    @SerialName("pincode") val pincode: String? = null,
    @SerialName("accountSecretKey") val accountSecretKey: String? = null
)

@Serializable
data class ArsenSignUpRequestDto(
    @SerialName("name") val name: String,
    @SerialName("outletName") val outletName: String,
    @SerialName("mobileNo") val mobileNo: String,
    @SerialName("emailID") val emailID: String,
    @SerialName("address") val address: String,
    @SerialName("pincode") val pincode: String,
    @SerialName("countryCode") val countryCode: String = "+91",
    @SerialName("referalID") val referalID: Int = 1,
    @SerialName("referalIDstr") val referalIDstr: String = "1",
    @SerialName("roleID") val roleID: Int = 3
)

@Serializable
data class ArsenSignUpWrapperDto(
    @SerialName("userCreate") val userCreate: ArsenSignUpRequestDto
)

@Serializable
data class ArsenSignUpResponseDto(
    @SerialName("statuscode") val statusCode: Int? = null,
    @SerialName("msg") val message: String? = null,
    @SerialName("userId") val userId: String? = null,
    @SerialName("userName") val userName: String? = null,
    @SerialName("password") val password: String? = null
)

@Serializable
data class LoginRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)

@Serializable
data class SignupRequestDto(
    @SerialName("full_name") val fullName: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)

@Serializable
data class AuthResponseDto(
    @SerialName("user_id") val userId: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("email") val email: String,
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String
) {
    fun toDomain(): User {
        return User(
            id = userId,
            fullName = fullName,
            email = email,
            token = AuthToken(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        )
    }
}
