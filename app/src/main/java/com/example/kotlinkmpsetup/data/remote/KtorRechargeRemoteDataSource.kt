package com.example.kotlinkmpsetup.data.remote

import com.example.kotlinkmpsetup.data.remote.dto.BillReqDto
import com.example.kotlinkmpsetup.data.remote.dto.CircleListResponseDto
import com.example.kotlinkmpsetup.data.remote.dto.DTHPlanListResponseDto
import com.example.kotlinkmpsetup.data.remote.dto.DthInfoResponseDto
import com.example.kotlinkmpsetup.data.remote.dto.FetchBillResponseDto
import com.example.kotlinkmpsetup.data.remote.dto.HLRLookUpResponseDto
import com.example.kotlinkmpsetup.data.remote.dto.OperatorListResponseDto
import com.example.kotlinkmpsetup.data.remote.dto.OperatorOptionalResponseDto
import com.example.kotlinkmpsetup.data.remote.dto.PlanListResponseDto
import com.example.kotlinkmpsetup.data.remote.dto.TransactionResponseDto
import com.example.kotlinkmpsetup.domain.model.BillFetchResult
import com.example.kotlinkmpsetup.domain.model.CircleModel
import com.example.kotlinkmpsetup.domain.model.DthCustomerInfo
import com.example.kotlinkmpsetup.domain.model.HlrLookUpResult
import com.example.kotlinkmpsetup.domain.model.NetworkResult
import com.example.kotlinkmpsetup.domain.model.OfferPlanModel
import com.example.kotlinkmpsetup.domain.model.OpOptionalOptionModel
import com.example.kotlinkmpsetup.domain.model.OperatorModel
import com.example.kotlinkmpsetup.domain.model.OperatorParamModel
import com.example.kotlinkmpsetup.domain.model.OptionalOperatorDataModel
import com.example.kotlinkmpsetup.domain.model.PlanDetailModel
import com.example.kotlinkmpsetup.domain.model.PlanTypeModel
import com.example.kotlinkmpsetup.domain.model.TransactionResultModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DATA (Recharge Remote Data Source)
 * ====================================================================================
 * Handles GetOperatorsActive, GetCircles, GetOperatorOptionals, HLR LookUp, Heavy Refresh, Simple Plans, DTH Simple Plans, R-Offers, FetchBill, Transaction, and DTH Info.
 */
class KtorRechargeRemoteDataSource(
    private val client: HttpClient
) {

    suspend fun getOperators(serviceId: Int): NetworkResult<List<OperatorModel>> {
        val result = safeApiCall<OperatorListResponseDto> {
            client.post(ApiEndpoints.Services.GET_OPERATORS) {
                setBody(mapOf("OType" to serviceId))
            }.body()
        }

        return when (result) {
            is NetworkResult.Success -> {
                val dto = result.data
                val operators = dto.operators?.map {
                    OperatorModel(
                        oid = it.oid ?: 0,
                        name = it.name.orEmpty(),
                        image = it.image.orEmpty(),
                        accountName = it.accountName ?: "Number",
                        lengthMin = it.length ?: 5,
                        lengthMax = it.lengthMax ?: 15,
                        isBilling = it.isBilling ?: false,
                        isAccountNumeric = it.isAccountNumeric ?: true,
                        isTakeCustomerNum = it.isTakeCustomerNum ?: false,
                        opType = it.opType ?: 0
                    )
                }.orEmpty()
                NetworkResult.Success(operators)
            }
            is NetworkResult.Error -> result
            is NetworkResult.NetworkOffline -> NetworkResult.NetworkOffline
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.Pending -> NetworkResult.Pending
        }
    }

    suspend fun getCircles(): NetworkResult<List<CircleModel>> {
        val result = safeApiCall<CircleListResponseDto> {
            client.post(ApiEndpoints.Services.GET_CIRCLES).body()
        }

        return when (result) {
            is NetworkResult.Success -> {
                val circles = result.data.circles?.map {
                    CircleModel(
                        id = it.id ?: 0,
                        name = it.circle.orEmpty()
                    )
                }.orEmpty()
                NetworkResult.Success(circles)
            }
            is NetworkResult.Error -> result
            is NetworkResult.NetworkOffline -> NetworkResult.NetworkOffline
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.Pending -> NetworkResult.Pending
        }
    }

    suspend fun getHlrLookUp(mobileNumber: String): NetworkResult<HlrLookUpResult> {
        val result = safeApiCall<HLRLookUpResponseDto> {
            client.post(ApiEndpoints.Services.GET_HLR_LOOKUP) {
                setBody(mapOf("mobile" to mobileNumber))
            }.body()
        }

        return when (result) {
            is NetworkResult.Success -> {
                val dto = result.data
                if (dto.statusCode == 1) {
                    NetworkResult.Success(HlrLookUpResult(oid = dto.oid ?: 0, circleId = dto.circleID ?: 0))
                } else {
                    NetworkResult.Error(dto.message ?: "HLR Lookup failed")
                }
            }
            is NetworkResult.Error -> result
            is NetworkResult.NetworkOffline -> NetworkResult.NetworkOffline
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.Pending -> NetworkResult.Pending
        }
    }

    suspend fun getDthHeavyRefresh(accountNo: String, oid: Int): NetworkResult<String> {
        val result = safeApiCall<DthInfoResponseDto> {
            client.post(ApiEndpoints.Services.GET_RNP_DTH_HEAVY_REFRESH) {
                setBody(mapOf("accountNo" to accountNo, "oid" to oid.toString()))
            }.body()
        }

        return when (result) {
            is NetworkResult.Success -> {
                val dto = result.data
                val responseMsg = dto.dthhrData?.response ?: dto.message ?: "Heavy refresh request captured successfully."
                NetworkResult.Success(responseMsg)
            }
            is NetworkResult.Error -> result
            is NetworkResult.NetworkOffline -> NetworkResult.NetworkOffline
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.Pending -> NetworkResult.Pending
        }
    }

    suspend fun getRechargePlans(circleId: Int, oid: Int): NetworkResult<List<PlanTypeModel>> {
        val result = safeApiCall<PlanListResponseDto> {
            client.post(ApiEndpoints.Services.RECH_SIMPLE_PLAN) {
                setBody(mapOf("circleID" to circleId, "oid" to oid))
            }.body()
        }

        return when (result) {
            is NetworkResult.Success -> {
                val dto = result.data
                val plans = dto.data?.types?.map { type ->
                    PlanTypeModel(
                        planType = type.pType.orEmpty(),
                        details = type.pDetails?.map { det ->
                            PlanDetailModel(
                                desc = det.desc.orEmpty(),
                                price = det.rs.orEmpty(),
                                validity = det.validity.orEmpty()
                            )
                        }.orEmpty()
                    )
                }.orEmpty()
                NetworkResult.Success(plans)
            }
            is NetworkResult.Error -> result
            is NetworkResult.NetworkOffline -> NetworkResult.NetworkOffline
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.Pending -> NetworkResult.Pending
        }
    }

    suspend fun getDTHSimplePlan(oid: Int, accountNo: String = ""): NetworkResult<List<PlanTypeModel>> {
        val result = safeApiCall<DTHPlanListResponseDto> {
            client.post(ApiEndpoints.Services.DTH_SIMPLE_PLAN) {
                setBody(mapOf("oid" to oid.toString(), "accountNo" to accountNo))
            }.body()
        }

        return when (result) {
            is NetworkResult.Success -> {
                val dto = result.data
                val plans = dto.data?.map { dthGroup ->
                    PlanTypeModel(
                        planType = dthGroup.pType.orEmpty(),
                        details = dthGroup.pDetials?.map { detail ->
                            PlanDetailModel(
                                desc = "${detail.effectiveName}: ${detail.effectiveDesc}",
                                price = detail.price?.monthly ?: detail.price?.yearly ?: detail.price?.rs ?: "0",
                                validity = detail.pLangauge.orEmpty()
                            )
                        }.orEmpty()
                    )
                }.orEmpty()
                NetworkResult.Success(plans)
            }
            is NetworkResult.Error -> result
            is NetworkResult.NetworkOffline -> NetworkResult.NetworkOffline
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.Pending -> NetworkResult.Pending
        }
    }

    suspend fun getDthPlanByLang(oid: Int, language: String, accountNo: String = ""): NetworkResult<List<PlanTypeModel>> {
        val result = safeApiCall<DTHPlanListResponseDto> {
            client.post(ApiEndpoints.Services.GET_DTH_PLAN_BY_LANG) {
                setBody(mapOf("oid" to oid.toString(), "Language" to language, "accountNo" to accountNo))
            }.body()
        }

        return when (result) {
            is NetworkResult.Success -> {
                val dto = result.data
                val plans = dto.data?.map { dthGroup ->
                    PlanTypeModel(
                        planType = dthGroup.pType.orEmpty(),
                        details = dthGroup.pDetials?.map { detail ->
                            PlanDetailModel(
                                desc = "${detail.effectiveName}: ${detail.effectiveDesc}",
                                price = detail.price?.monthly ?: detail.price?.yearly ?: detail.price?.rs ?: "0",
                                validity = detail.pLangauge.orEmpty()
                            )
                        }.orEmpty()
                    )
                }.orEmpty()
                NetworkResult.Success(plans)
            }
            is NetworkResult.Error -> result
            is NetworkResult.NetworkOffline -> NetworkResult.NetworkOffline
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.Pending -> NetworkResult.Pending
        }
    }

    suspend fun getRNPRoffer(accountNo: String, oid: Int): NetworkResult<List<OfferPlanModel>> {
        val result = safeApiCall<PlanListResponseDto> {
            client.post(ApiEndpoints.Services.GET_RNP_ROFFER) {
                setBody(mapOf("accountNo" to accountNo, "oid" to oid.toString()))
            }.body()
        }

        return when (result) {
            is NetworkResult.Success -> {
                val dto = result.data
                val offers = dto.rofferData?.map { offer ->
                    OfferPlanModel(
                        amount = offer.amount.orEmpty(),
                        description = offer.description.orEmpty()
                    )
                }.orEmpty()
                NetworkResult.Success(offers)
            }
            is NetworkResult.Error -> result
            is NetworkResult.NetworkOffline -> NetworkResult.NetworkOffline
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.Pending -> NetworkResult.Pending
        }
    }

    suspend fun getOperatorOptionals(oid: Int): NetworkResult<OptionalOperatorDataModel> {
        val result = safeApiCall<OperatorOptionalResponseDto> {
            client.post(ApiEndpoints.Services.GET_OPERATOR_OPTIONALS) {
                setBody(mapOf("oid" to oid.toString()))
            }.body()
        }

        return when (result) {
            is NetworkResult.Success -> {
                val dto = result.data.data
                val params = dto?.operatorParams?.map {
                    OperatorParamModel(
                        id = it.id ?: 0,
                        paramName = it.paramName.orEmpty(),
                        dataType = it.dataType ?: "TEXT",
                        isDropDown = it.isDropDown ?: false,
                        isCustomerNo = it.isCustomerNo ?: false,
                        remark = it.remark.orEmpty()
                    )
                }.orEmpty()

                val options = dto?.opOptionalDic?.map {
                    OpOptionalOptionModel(
                        optionalId = it.optionalID ?: 0,
                        value = it.value.orEmpty()
                    )
                }.orEmpty()

                NetworkResult.Success(
                    OptionalOperatorDataModel(
                        operatorParams = params,
                        dropdownOptions = options
                    )
                )
            }
            is NetworkResult.Error -> result
            is NetworkResult.NetworkOffline -> NetworkResult.NetworkOffline
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.Pending -> NetworkResult.Pending
        }
    }

    suspend fun fetchBill(req: BillReqDto): NetworkResult<BillFetchResult> {
        val result = safeApiCall<FetchBillResponseDto> {
            client.post(ApiEndpoints.Services.FETCH_BILL) {
                setBody(req)
            }.body()
        }

        return when (result) {
            is NetworkResult.Success -> {
                val dto = result.data
                val bbps = dto.bbpsResponse
                if (dto.statusCode == 1 && bbps != null && bbps.statusCode == 1) {
                    NetworkResult.Success(
                        BillFetchResult(
                            amount = bbps.amount ?: 0.0,
                            customerName = bbps.customerName.orEmpty(),
                            billNumber = bbps.billNumber.orEmpty(),
                            billDate = bbps.billDate.orEmpty(),
                            dueDate = bbps.dueDate.orEmpty(),
                            fetchBillId = bbps.fetchBillID ?: 0,
                            referenceId = bbps.referenceID.orEmpty()
                        )
                    )
                } else {
                    NetworkResult.Error(bbps?.message ?: dto.message ?: "Failed to fetch bill details")
                }
            }
            is NetworkResult.Error -> result
            is NetworkResult.NetworkOffline -> NetworkResult.NetworkOffline
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.Pending -> NetworkResult.Pending
        }
    }

    suspend fun doTransaction(req: BillReqDto): NetworkResult<TransactionResultModel> {
        val result = safeApiCall<TransactionResponseDto> {
            client.post(ApiEndpoints.Services.TRANSACTION) {
                setBody(req)
            }.body()
        }

        return when (result) {
            is NetworkResult.Success -> {
                val dto = result.data
                if (dto.statusCode != -1 && dto.statusCode != 3) {
                    NetworkResult.Success(
                        TransactionResultModel(
                            transactionId = dto.transactionID.orEmpty(),
                            liveId = dto.liveID.orEmpty(),
                            statusCode = dto.statusCode ?: 1,
                            message = dto.message ?: "Transaction Successful",
                            commAmount = dto.commAmount ?: 0.0
                        )
                    )
                } else {
                    NetworkResult.Error(dto.message ?: "Transaction failed")
                }
            }
            is NetworkResult.Error -> result
            is NetworkResult.NetworkOffline -> NetworkResult.NetworkOffline
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.Pending -> NetworkResult.Pending
        }
    }

    suspend fun getDthCustomerInfo(accountNo: String, oid: Int): NetworkResult<List<DthCustomerInfo>> {
        val result = safeApiCall<DthInfoResponseDto> {
            client.post(ApiEndpoints.Services.GET_RNP_DTH_CUST_INFO) {
                setBody(mapOf("accountNo" to accountNo, "oid" to oid.toString()))
            }.body()
        }

        return when (result) {
            is NetworkResult.Success -> {
                val dto = result.data
                val infoList = dto.dthciData?.data?.map {
                    DthCustomerInfo(
                        key = it.key.orEmpty(),
                        value = it.value.orEmpty()
                    )
                }.orEmpty()
                NetworkResult.Success(infoList)
            }
            is NetworkResult.Error -> result
            is NetworkResult.NetworkOffline -> NetworkResult.NetworkOffline
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.Pending -> NetworkResult.Pending
        }
    }
}
