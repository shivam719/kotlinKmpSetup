package com.example.kotlinkmpsetup.domain.model

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DOMAIN (Recharge & Bill Payment Models)
 * ====================================================================================
 */

data class CircleModel(
    val id: Int = 0,
    val name: String = ""
)

data class OperatorModel(
    val oid: Int = 0,
    val name: String = "",
    val image: String = "",
    val accountName: String = "Number",
    val lengthMin: Int = 10,
    val lengthMax: Int = 10,
    val isBilling: Boolean = false,
    val isAccountNumeric: Boolean = true,
    val isTakeCustomerNum: Boolean = false,
    val opType: Int = 0
)

data class HlrLookUpResult(
    val oid: Int = 0,
    val circleId: Int = 0
)

data class PlanDetailModel(
    val desc: String = "",
    val price: String = "",
    val validity: String = ""
)

data class PlanTypeModel(
    val planType: String = "",
    val details: List<PlanDetailModel> = emptyList()
)

data class OfferPlanModel(
    val amount: String = "",
    val description: String = ""
)

data class OperatorParamModel(
    val id: Int = 0,
    val paramName: String = "",
    val dataType: String = "TEXT",
    val isDropDown: Boolean = false,
    val isCustomerNo: Boolean = false,
    val remark: String = ""
)

data class OpOptionalOptionModel(
    val optionalId: Int = 0,
    val value: String = ""
)

data class OptionalOperatorDataModel(
    val operatorParams: List<OperatorParamModel> = emptyList(),
    val dropdownOptions: List<OpOptionalOptionModel> = emptyList()
)

data class BillFetchResult(
    val amount: Double = 0.0,
    val customerName: String = "",
    val billNumber: String = "",
    val billDate: String = "",
    val dueDate: String = "",
    val fetchBillId: Int = 0,
    val referenceId: String = ""
)

data class TransactionResultModel(
    val transactionId: String = "",
    val liveId: String = "",
    val statusCode: Int = 0,
    val message: String = "",
    val commAmount: Double = 0.0
)

data class DthCustomerInfo(
    val key: String = "",
    val value: String = ""
)

data class TransactionReceipt(
    val operatorName: String = "Provider",
    val mainAccountNo: String = "Account Number",
    val amount: String = "0.00",
    val statusMsg: String = "Transaction Successful",
    val statusCode: Int = 1,
    val transactionId: String = "",
    val referenceId: String = "",
    val date: String = "",
    val commAmount: Double = 0.0,
    val shopName: String = "Rehan Telecom Center",
    val shopDetails: String = "Outlet ID: 10004 | West Bengal"
)
