package com.example.kotlinkmpsetup.domain.repository

import com.example.kotlinkmpsetup.data.remote.dto.BillReqDto
import com.example.kotlinkmpsetup.domain.model.BillFetchResult
import com.example.kotlinkmpsetup.domain.model.CircleModel
import com.example.kotlinkmpsetup.domain.model.DthCustomerInfo
import com.example.kotlinkmpsetup.domain.model.HlrLookUpResult
import com.example.kotlinkmpsetup.domain.model.NetworkResult
import com.example.kotlinkmpsetup.domain.model.OfferPlanModel
import com.example.kotlinkmpsetup.domain.model.OperatorModel
import com.example.kotlinkmpsetup.domain.model.OptionalOperatorDataModel
import com.example.kotlinkmpsetup.domain.model.PlanTypeModel
import com.example.kotlinkmpsetup.domain.model.TransactionResultModel

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DOMAIN (Recharge Repository Interface)
 * ====================================================================================
 */
interface RechargeRepository {
    suspend fun getOperators(serviceId: Int): NetworkResult<List<OperatorModel>>
    suspend fun getCircles(): NetworkResult<List<CircleModel>>
    suspend fun getHlrLookUp(mobileNumber: String): NetworkResult<HlrLookUpResult>
    suspend fun getDthHeavyRefresh(accountNo: String, oid: Int): NetworkResult<String>
    suspend fun getRechargePlans(circleId: Int, oid: Int): NetworkResult<List<PlanTypeModel>>
    suspend fun getDTHSimplePlan(oid: Int, accountNo: String = ""): NetworkResult<List<PlanTypeModel>>
    suspend fun getDthPlanByLang(oid: Int, language: String, accountNo: String = ""): NetworkResult<List<PlanTypeModel>>
    suspend fun getRNPRoffer(accountNo: String, oid: Int): NetworkResult<List<OfferPlanModel>>
    suspend fun getOperatorOptionals(oid: Int): NetworkResult<OptionalOperatorDataModel>
    suspend fun fetchBill(req: BillReqDto): NetworkResult<BillFetchResult>
    suspend fun doTransaction(req: BillReqDto): NetworkResult<TransactionResultModel>
    suspend fun getDthCustomerInfo(accountNo: String, oid: Int): NetworkResult<List<DthCustomerInfo>>
}
