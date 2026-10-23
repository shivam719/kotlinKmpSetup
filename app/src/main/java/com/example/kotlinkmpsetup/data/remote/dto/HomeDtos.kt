package com.example.kotlinkmpsetup.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DATA (Home DTOs - Data Transfer Objects)
 * ====================================================================================
 * Fully null-safe DTOs for ArsenPay / RMS Recharge Home APIs:
 * - App/GetBalance
 * - App/GetAppBanner
 * - App/GetAppNews
 * - App/GetOpTypes
 */

@Serializable
data class ArsenBalanceDataDto(
    @SerialName("balance") val balance: Double? = null,
    @SerialName("bBalance") val bBalance: Double? = null,
    @SerialName("cBalance") val cBalance: Double? = null,
    @SerialName("uBalance") val uBalance: Double? = null,
    @SerialName("prepaidWalletName") val prepaidWalletName: String? = null,
    @SerialName("bankWalletName") val bankWalletName: String? = null
)

@Serializable
data class ArsenBalanceResponseDto(
    @SerialName("statuscode") val statusCode: Int? = null,
    @SerialName("msg") val message: String? = null,
    @SerialName("data") val data: ArsenBalanceDataDto? = null
)

@Serializable
data class ArsenBannerDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("resourceUrl") val resourceUrl: String? = null,
    @SerialName("siteResourceUrl") val siteResourceUrl: String? = null,
    @SerialName("bannerName") val bannerName: String? = null
)

@Serializable
data class ArsenBannerResponseDto(
    @SerialName("statuscode") val statusCode: Int? = null,
    @SerialName("msg") val message: String? = null,
    @SerialName("banners") val banners: List<ArsenBannerDto>? = null
)

@Serializable
data class ArsenNewsDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("newsContent") val newsContent: String? = null,
    @SerialName("newsDetail") val newsDetail: String? = null,
    @SerialName("title") val title: String? = null
)

@Serializable
data class ArsenNewsResponseDto(
    @SerialName("statuscode") val statusCode: Int? = null,
    @SerialName("msg") val message: String? = null,
    @SerialName("newsContent") val newsContent: ArsenNewsDto? = null,
    @SerialName("news") val news: String? = null
)

@Serializable
data class ArsenServiceItemDto(
    @SerialName("serviceID") val serviceID: Int? = null,
    @SerialName("parentID") val parentID: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("service") val service: String? = null,
    @SerialName("sCode") val sCode: String? = null,
    @SerialName("icon") val icon: String? = null,
    @SerialName("enabled") val enabled: Boolean? = null,
    @SerialName("isActive") val isActive: Boolean? = null,
    @SerialName("isServiceActive") val isServiceActive: Boolean? = null,
    @SerialName("serviceTypeID") val serviceTypeID: String? = null,
    @SerialName("opTypeID") val opTypeID: Int? = null,
    @SerialName("category") val category: String? = null
)

@Serializable
data class ArsenServicesDataDto(
    @SerialName("assignedOpTypes") val assignedOpTypes: List<ArsenServiceItemDto>? = null,
    @SerialName("opTypes") val opTypes: List<ArsenServiceItemDto>? = null
)

@Serializable
data class ArsenServicesResponseDto(
    @SerialName("statuscode") val statusCode: Int? = null,
    @SerialName("msg") val message: String? = null,
    @SerialName("data") val data: ArsenServicesDataDto? = null,
    @SerialName("assignedOpTypes") val assignedOpTypes: List<ArsenServiceItemDto>? = null,
    @SerialName("opTypes") val opTypes: List<ArsenServiceItemDto>? = null,
    @SerialName("isAddMoneyEnable") val isAddMoneyEnable: Boolean? = null,
    @SerialName("isPaymentGatway") val isPaymentGatway: Boolean? = null,
    @SerialName("isUPI") val isUPI: Boolean? = null
)
