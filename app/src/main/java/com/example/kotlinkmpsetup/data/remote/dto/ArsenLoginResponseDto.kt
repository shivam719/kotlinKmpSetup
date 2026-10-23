package com.example.kotlinkmpsetup.data.remote.dto

import com.example.kotlinkmpsetup.domain.model.AuthToken
import com.example.kotlinkmpsetup.domain.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArsenLoginResponseDto(
    @SerialName("statuscode") val statusCode: Int? = null,
    @SerialName("msg") val message: String? = null,
    @SerialName("isOTPRequired") val isOTPRequired: Boolean? = false,
    @SerialName("isVersionValid") val isVersionValid: Boolean? = true,
    @SerialName("isAppValid") val isAppValid: Boolean? = true,
    @SerialName("checkID") val checkID: Int? = null,
    @SerialName("otpSession") val otpSession: String? = null,
    @SerialName("oTPSession") val oTPSession: String? = null,
    @SerialName("data") val data: ArsenUserDataDto? = null
) {
    val effectiveOtpSession: String?
        get() = otpSession ?: oTPSession ?: checkID?.toString()
    fun toDomain(): User {
        val userPayload = data
        val sessionString = userPayload?.sessionToken ?: "session_demo_token"
        val sessionIdString = userPayload?.sessionID?.toString() ?: ""
        return User(
            id = userPayload?.userID?.toString() ?: "17",
            sessionID = sessionIdString,
            fullName = userPayload?.name ?: "Rehan Telecom",
            email = userPayload?.emailID ?: userPayload?.mobileNo ?: "",
            token = AuthToken(
                accessToken = sessionString,
                refreshToken = userPayload?.accountSecretKey ?: sessionString
            )
        )
    }
}
