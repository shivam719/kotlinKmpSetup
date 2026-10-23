package com.example.kotlinkmpsetup.data.repository

import com.example.kotlinkmpsetup.data.remote.KtorRechargeRemoteDataSource
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
import com.example.kotlinkmpsetup.domain.repository.RechargeRepository

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DATA (Recharge Repository Implementation)
 * ====================================================================================
 */
class RechargeRepositoryImpl(
    private val remoteDataSource: KtorRechargeRemoteDataSource
) : RechargeRepository {

    override suspend fun getOperators(serviceId: Int): NetworkResult<List<OperatorModel>> {
        return remoteDataSource.getOperators(serviceId)
    }

    override suspend fun getCircles(): NetworkResult<List<CircleModel>> {
        return remoteDataSource.getCircles()
    }

    override suspend fun getHlrLookUp(mobileNumber: String): NetworkResult<HlrLookUpResult> {
        return remoteDataSource.getHlrLookUp(mobileNumber)
    }

    override suspend fun getDthHeavyRefresh(accountNo: String, oid: Int): NetworkResult<String> {
        return remoteDataSource.getDthHeavyRefresh(accountNo, oid)
    }

    override suspend fun getRechargePlans(circleId: Int, oid: Int): NetworkResult<List<PlanTypeModel>> {
        return remoteDataSource.getRechargePlans(circleId, oid)
    }

    override suspend fun getDTHSimplePlan(oid: Int, accountNo: String): NetworkResult<List<PlanTypeModel>> {
        return remoteDataSource.getDTHSimplePlan(oid, accountNo)
    }

    override suspend fun getDthPlanByLang(oid: Int, language: String, accountNo: String): NetworkResult<List<PlanTypeModel>> {
        return remoteDataSource.getDthPlanByLang(oid, language, accountNo)
    }

    override suspend fun getRNPRoffer(accountNo: String, oid: Int): NetworkResult<List<OfferPlanModel>> {
        return remoteDataSource.getRNPRoffer(accountNo, oid)
    }

    override suspend fun getOperatorOptionals(oid: Int): NetworkResult<OptionalOperatorDataModel> {
        return remoteDataSource.getOperatorOptionals(oid)
    }

    override suspend fun fetchBill(req: BillReqDto): NetworkResult<BillFetchResult> {
        return remoteDataSource.fetchBill(req)
    }

    override suspend fun doTransaction(req: BillReqDto): NetworkResult<TransactionResultModel> {
        return remoteDataSource.doTransaction(req)
    }

    override suspend fun getDthCustomerInfo(accountNo: String, oid: Int): NetworkResult<List<DthCustomerInfo>> {
        return remoteDataSource.getDthCustomerInfo(accountNo, oid)
    }
}
