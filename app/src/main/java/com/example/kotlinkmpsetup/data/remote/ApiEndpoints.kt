package com.example.kotlinkmpsetup.data.remote

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DATA (Centralized API Endpoints & Network Constants)
 * ====================================================================================
 * Centralized Single Source of Truth for Network Base URLs, Endpoints, Headers,
 * Query Parameters, and HTTP Status Codes.
 *
 * INTERVIEW POINT:
 * Centralizing API Constants prevents hardcoded string typos across repositories/data sources,
 * simplifies environment switching (Dev, Staging, Production), and provides a clean, maintainable
 * contract for network operations.
 */
object ApiEndpoints {

    // ====================================================================================
    // BASE ENVIRONMENT URLS
    // ====================================================================================
    const val BASE_URL_DEV = "https://rmsrecharge.com"

    /** Active Base URL used by Ktor HttpClient */
    const val BASE_URL = BASE_URL_DEV

    /** App ID & Domain for ArsenPay API */
    const val APP_DOMAIN = "rmsrecharge.com"
    const val APP_ID = "ROUNDPAYAPPID13APR20191351"

    // ====================================================================================
    // NETWORK TIMEOUT CONFIGURATION (IN SECONDS)
    // ====================================================================================
    const val CONNECT_TIMEOUT_SECONDS = 15L
    const val READ_TIMEOUT_SECONDS = 15L
    const val WRITE_TIMEOUT_SECONDS = 15L

    // ====================================================================================
    // AUTHENTICATION ENDPOINT ROUTES
    // ====================================================================================
    object Auth {
        const val LOGIN = "/App/Login"
        const val VALIDATE_OTP = "/App/ValidateOTP"
        const val SIGNUP = "/App/AppUserSignup"
        const val REFRESH_TOKEN = "/auth/refresh"
        const val LOGOUT = "/auth/logout"
        const val FORGOT_PASSWORD = "/auth/forgot-password"
        const val VERIFY_EMAIL = "/auth/verify-email"
    }

    // ====================================================================================
    // USER PROFILE ENDPOINT ROUTES
    // ====================================================================================
    object User {
        const val PROFILE = "/user/profile"
        const val UPDATE_PROFILE = "/user/update"
        const val CHANGE_PASSWORD = "/user/change-password"
    }

    // ====================================================================================
    // HOME DASHBOARD ENDPOINT ROUTES
    // ====================================================================================
    object Home {
        const val GET_BALANCE = "/App/GetBalance"
        const val GET_APP_BANNER = "/App/GetAppBanner"
        const val GET_APP_NEWS = "/App/GetAppNews"
        const val GET_OP_TYPES = "/App/GetOpTypes"
        const val GET_COMPANY_PROFILE = "/App/GetCompanyProfile"
    }

    // ====================================================================================
    // RECHARGE & BILL PAYMENT ENDPOINT ROUTES
    // ====================================================================================
    object Services {
        const val IMAGE_URL: String = "https://$APP_DOMAIN/Image/operator/"
        const val GET_OPERATORS = "/App/GetOperatorsActive"
        const val GET_OPERATOR_OPTIONALS = "/App/GetOperatorOptionals"
        const val RECH_SIMPLE_PLAN = "/App/RechSimplePlan"
        const val DTH_SIMPLE_PLAN = "/App/GetDTHSimplePlan"
        const val GET_DTH_PLAN_BY_LANG = "/App/GetDTHPlanByLang"
        const val GET_DTH_CHANNEL_LIST = "/App/GetDTHChannelList"
        const val GET_RNP_ROFFER = "/App/GetRNPRoffer"
        const val GET_HLR_LOOKUP = "/App/GetHLRLookUp"
        const val GET_RNP_DTH_CUST_INFO = "/App/GetRNPDTHCustInfo"
        const val GET_RNP_DTH_HEAVY_REFRESH = "/App/GetRNPDTHHeavyRefresh"
        const val FETCH_BILL = "/App/FetchBill"
        const val TRANSACTION = "/App/Transaction"
        const val GET_CIRCLES = "/App/GetCircles"
    }

    // ====================================================================================
    // STANDARD HTTP HEADERS
    // ====================================================================================
    object Headers {
        const val AUTHORIZATION = "Authorization"
        const val BEARER_PREFIX = "Bearer "
        const val CONTENT_TYPE = "Content-Type"
        const val ACCEPT = "Accept"
        const val APPLICATION_JSON = "application/json"
        const val ACCEPT_LANGUAGE = "Accept-Language"
        const val X_APP_VERSION = "X-App-Version"
        const val X_DEVICE_ID = "X-Device-Id"
        const val X_PLATFORM = "X-Platform"
    }

    // ====================================================================================
    // COMMON HTTP QUERY PARAMETERS
    // ====================================================================================
    object QueryParams {
        const val PAGE = "page"
        const val LIMIT = "limit"
        const val SEARCH = "query"
        const val SORT_BY = "sort_by"
    }

    // ====================================================================================
    // HTTP STATUS CODES
    // ====================================================================================
    object HttpStatus {
        const val OK = 200
        const val CREATED = 201
        const val ACCEPTED = 202
        const val BAD_REQUEST = 400
        const val UNAUTHORIZED = 401
        const val FORBIDDEN = 403
        const val NOT_FOUND = 404
        const val CONFLICT = 409
        const val UNPROCESSABLE_ENTITY = 422
        const val INTERNAL_SERVER_ERROR = 500
        const val BAD_GATEWAY = 502
        const val SERVICE_UNAVAILABLE = 503
    }

    // ====================================================================================
    // COMMON JSON RESPONSE KEYS
    // ====================================================================================
    object ResponseKeys {
        const val ACCESS_TOKEN = "access_token"
        const val REFRESH_TOKEN = "refresh_token"
        const val ERROR_MESSAGE = "message"
        const val STATUS_CODE = "status_code"
    }
}
