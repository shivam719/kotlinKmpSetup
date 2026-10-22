package com.example.kotlinkmpsetup.data.remote

import android.util.Log
import com.example.kotlinkmpsetup.BuildConfig
import com.example.kotlinkmpsetup.data.local.DeviceInfoProvider
import com.example.kotlinkmpsetup.data.local.TokenDataStoreManager
import com.example.kotlinkmpsetup.data.remote.dto.AuthResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DATA (Ktor Engine & OkHttp Interceptor Configuration)
 * ====================================================================================
 * Ktor Client Builder configured with OkHttp Engine, OkHttp HttpLoggingInterceptor,
 * custom timing interceptor, ContentNegotiation, and Bearer Token Authorization.
 *
 * INTERVIEW POINT:
 * 1. **OkHttp Engine Interceptors**: Ktor's OkHttp engine permits adding native OkHttp `Interceptor`
 *    and `HttpLoggingInterceptor` (Level.BODY) directly into the network stack.
 * 2. **Debug Mode Scoping**: Logging is enabled only when `BuildConfig.DEBUG == true` to prevent
 *    sensitive Bearer tokens or PII data leakage in production Release builds.
 * 3. **Automatic Refresh**: Ktor `Auth` bearer plugin automatically intercepts 401 Unauthorized,
 *    refreshes the access token using the refresh token, updates storage, and retries the call.
 */
object KtorAuthClient {

    fun createHttpClient(
        tokenManager: TokenDataStoreManager,
        deviceInfoProvider: DeviceInfoProvider
    ): HttpClient {
        return HttpClient(OkHttp) {
            // Default Base URL and Content Type
            defaultRequest {
                url(ApiEndpoints.BASE_URL)
                contentType(ContentType.Application.Json)
            }

            // OkHttp Engine Interceptors (Active in Debug Mode)
            engine {
                // Auto Body Interceptor: Injects Latitude, Longitude, appid, domain, imei, serialNo, version into POST requests
                addInterceptor(AutoBodyInterceptor(deviceInfoProvider, tokenManager))
                // OkHttp HttpLoggingInterceptor for Logcat Body & Header Logs
                addInterceptor(
                    HttpLoggingInterceptor { message ->
                        if (BuildConfig.DEBUG) {
                            Log.d("OkHttpNetworkLog", message)
                        }
                    }.apply {
                        level = if (BuildConfig.DEBUG) {
                            HttpLoggingInterceptor.Level.BODY
                        } else {
                            HttpLoggingInterceptor.Level.NONE
                        }
                    }
                )

                config {
                    connectTimeout(ApiEndpoints.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    readTimeout(ApiEndpoints.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    writeTimeout(ApiEndpoints.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                }
            }

            // Custom Interceptor Plugin for Header Injection
            applyGlobalInterceptors()

            // Content Negotiation with Kotlinx Serialization
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                    encodeDefaults = true
                })
            }

            // Bearer Token Authentication & Automatic Refresh Handler
            install(Auth) {
                bearer {
                    loadTokens {
                        val access = tokenManager.getAccessToken()
                        val refresh = tokenManager.getRefreshToken()
                        if (access != null && refresh != null) {
                            BearerTokens(accessToken = access, refreshToken = refresh)
                        } else null
                    }

                    refreshTokens {
                        val currentRefreshToken = tokenManager.getRefreshToken() ?: return@refreshTokens null
                        try {
                            // Call refresh token API endpoint
                            val response: AuthResponseDto = client.post(ApiEndpoints.Auth.REFRESH_TOKEN) {
                                setBody(mapOf("refresh_token" to currentRefreshToken))
                            }.body()

                            tokenManager.saveTokens(
                                accessToken = response.accessToken,
                                refreshToken = response.refreshToken
                            )

                            BearerTokens(
                                accessToken = response.accessToken,
                                refreshToken = response.refreshToken
                            )
                        } catch (e: Exception) {
                            tokenManager.clearTokens()
                            null
                        }
                    }
                }
            }
        }
    }
}
