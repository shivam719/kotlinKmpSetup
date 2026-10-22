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
    @SerialName("data") val data: ArsenUserDataDto? = null
) {
    fun toDomain(): User {
        val userPayload = data
        val tokenString = userPayload?.sessionToken ?: userPayload?.sessionID?.toString() ?: "session_demo_token"
        return User(
            id = userPayload?.userID?.toString() ?: "usr_17",
            fullName = userPayload?.name ?: "Rehan Telecom",
            email = userPayload?.emailID ?: userPayload?.mobileNo ?: "",
            token = AuthToken(
                accessToken = tokenString,
                refreshToken = userPayload?.accountSecretKey ?: tokenString
            )
        )
    }
}