package com.example.kotlinkmpsetup.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DATA (Recharge & Bill Payment DTOs)
 * ====================================================================================
 * DTOs for Operators, Circles, Bill Fetching, Recharge Transactions, DTH Info, Heavy Refresh, HLR LookUp, and Plans.
 */

@Serializable
data class CircleDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("circle") val circle: String? = null
)

@Serializable
data class CircleListResponseDto(
    @SerialName("statuscode") val statusCode: Int? = null,
    @SerialName("msg") val message: String? = null,
    @SerialName("cirlces") val circles: List<CircleDto>? = null
)

@Serializable
data class OperatorDto(
    @SerialName("oid") val oid: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("accountName") val accountName: String? = null,
    @SerialName("length") val length: Int? = null,
    @SerialName("lengthMax") val lengthMax: Int? = null,
    @SerialName("isBilling") val isBilling: Boolean? = false,
    @SerialName("isAccountNumeric") val isAccountNumeric: Boolean? = false,
    @SerialName("isTakeCustomerNum") val isTakeCustomerNum: Boolean? = false,
    @SerialName("opType") val opType: Int? = null
)

@Serializable
data class OperatorListResponseDto(
    @SerialName("statuscode") val statusCode: Int? = null,
    @SerialName("msg") val message: String? = null,
    @SerialName("operators") val operators: List<OperatorDto>? = null
)

@Serializable
data class BillReqDto(
    @SerialName("accountNo") val accountNo: String? = null,
    @SerialName("amount") val amount: String? = null,
    @SerialName("customerNo") val customerNo: String? = null,
    @SerialName("geoCode") val geoCode: String? = null,
    @SerialName("o1") val o1: String? = null,
    @SerialName("o2") val o2: String? = null,
    @SerialName("o3") val o3: String? = null,
    @SerialName("o4") val o4: String? = null,
    @SerialName("oid") val oid: String? = null,
    @SerialName("fetchBilID") val fetchBillID: Int? = null,
    @SerialName("isReal") val isReal: Boolean? = null,
    @SerialName("refID") val refID: String? = null,
    @SerialName("securityKey") val securityKey: String? = null
)

@Serializable
data class BBPSResponseDto(
    @SerialName("amount") val amount: Double? = null,
    @SerialName("customerName") val customerName: String? = null,
    @SerialName("billNumber") val billNumber: String? = null,
    @SerialName("billDate") val billDate: String? = null,
    @SerialName("dueDate") val dueDate: String? = null,
    @SerialName("fetchBillID") val fetchBillID: Int? = null,
    @SerialName("refferenceID") val referenceID: String? = null,
    @SerialName("statuscode") val statusCode: Int? = null,
    @SerialName("msg") val message: String? = null
)

@Serializable
data class FetchBillResponseDto(
    @SerialName("statuscode") val statusCode: Int? = null,
    @SerialName("msg") val message: String? = null,
    @SerialName("bBPSResponse") val bbpsResponse: BBPSResponseDto? = null
)

@Serializable
data class TransactionResponseDto(
    @SerialName("statuscode") val statusCode: Int? = null,
    @SerialName("msg") val message: String? = null,
    @SerialName("transactionID") val transactionID: String? = null,
    @SerialName("liveID") val liveID: String? = null,
    @SerialName("commAmount") val commAmount: Double? = null
)

@Serializable
data class DthInfoItemDto(
    @SerialName("key") val key: String? = null,
    @SerialName("value") val value: String? = null
)

@Serializable
data class DthciDataDto(
    @SerialName("statusCode") val statusCode: Int? = null,
    @SerialName("msg") val message: String? = null,
    @SerialName("data") val data: List<DthInfoItemDto>? = null
)

@Serializable
data class DthInfoResponseDto(
    @SerialName("statuscode") val statusCode: Int? = null,
    @SerialName("msg") val message: String? = null,
    @SerialName("dthciData") val dthciData: DthciDataDto? = null,
    @SerialName("dthhrData") val dthhrData: DthhrDataDto? = null
)

@Serializable
data class DthhrDataDto(
    @SerialName("statusCode") val statusCode: Int? = null,
    @SerialName("msg") val message: String? = null,
    @SerialName("response") val response: String? = null
)

@Serializable
data class HLRLookUpResponseDto(
    @SerialName("statuscode") val statusCode: Int? = null,
    @SerialName("msg") val message: String? = null,
    @SerialName("oid") val oid: Int? = null,
    @SerialName("circleID") val circleID: Int? = null
)

@Serializable
data class PlanDetailDto(
    @SerialName("desc") val desc: String? = null,
    @SerialName("rs") val rs: String? = null,
    @SerialName("validity") val validity: String? = null
)

@Serializable
data class PlanTypeDto(
    @SerialName("pType") val pType: String? = null,
    @SerialName("pDetails") val pDetails: List<PlanDetailDto>? = null
)

@Serializable
data class OfferPlanDto(
    @SerialName("amount") val amount: String? = null,
    @SerialName("description") val description: String? = null
)

@Serializable
data class PlanDataDto(
    @SerialName("types") val types: List<PlanTypeDto>? = null
)

@Serializable
data class PlanListResponseDto(
    @SerialName("statuscode") val statusCode: Int? = null,
    @SerialName("msg") val message: String? = null,
    @SerialName("data") val data: PlanDataDto? = null,
    @SerialName("rofferData") val rofferData: List<OfferPlanDto>? = null
)

@Serializable
data class DTHPriceDto(
    @SerialName("halfYearly") val halfYearly: String? = null,
    @SerialName("monthly") val monthly: String? = null,
    @SerialName("quarterly") val quarterly: String? = null,
    @SerialName("yearly") val yearly: String? = null,
    @SerialName("rs") val rs: String? = null
)

@Serializable
data class DTHPDetailDto(
    @SerialName("pChannelCount") val pChannelCount: Int? = null,
    @SerialName("pCount") val pCount: Int? = null,
    @SerialName("pDescription") val pDescription: String? = null,
    @SerialName("desc") val desc: String? = null,
    @SerialName("pLangauge") val pLangauge: String? = null,
    @SerialName("packageId") val packageId: Int? = null,
    @SerialName("packageName") val packageName: String? = null,
    @SerialName("plan_name") val planName: String? = null,
    @SerialName("price") val price: DTHPriceDto? = null
) {
    val effectiveName: String
        get() = packageName ?: planName ?: "DTH Package"

    val effectiveDesc: String
        get() = pDescription ?: desc ?: ""
}

@Serializable
data class DTHPlanDataDto(
    @SerialName("pType") val pType: String? = null,
    @SerialName("pDetials") val pDetials: List<DTHPDetailDto>? = null
)

@Serializable
data class DTHPlanListResponseDto(
    @SerialName("statuscode") val statusCode: Int? = null,
    @SerialName("msg") val message: String? = null,
    @SerialName("data") val data: List<DTHPlanDataDto>? = null
)

@Serializable
data class OperatorParamDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("paramName") val paramName: String? = null,
    @SerialName("dataType") val dataType: String? = null,
    @SerialName("isDropDown") val isDropDown: Boolean? = false,
    @SerialName("isCustomerNo") val isCustomerNo: Boolean? = false,
    @SerialName("remark") val remark: String? = null
)

@Serializable
data class OpOptionalDicDto(
    @SerialName("optionalID") val optionalID: Int? = null,
    @SerialName("value") val value: String? = null
)

@Serializable
data class OptionalOperatorDataDto(
    @SerialName("operatorParams") val operatorParams: List<OperatorParamDto>? = null,
    @SerialName("opOptionalDic") val opOptionalDic: List<OpOptionalDicDto>? = null
)

@Serializable
data class OperatorOptionalResponseDto(
    @SerialName("statuscode") val statusCode: Int? = null,
    @SerialName("msg") val message: String? = null,
    @SerialName("data") val data: OptionalOperatorDataDto? = null
)
