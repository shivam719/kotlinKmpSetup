package com.example.kotlinkmpsetup.data.remote

import android.util.Log
import com.example.kotlinkmpsetup.data.local.DeviceInfoProvider
import com.example.kotlinkmpsetup.data.local.TokenDataStoreManager
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import org.json.JSONObject

/**
 * ====================================================================================
 * KTOR / OKHTTP: AUTOMATIC PAYLOAD & LOCATION INTERCEPTOR
 * ====================================================================================
 * Intercepts outgoing HTTP POST/PUT requests and automatically injects:
 * - GPS Location (`Latitude`, `Longitude`)
 * - System Parameters (`appid`, `domain`, `imei`, `serialNo`, `version`, `regKey`, `loginTypeID`)
 * - Session Parameters (`session`, `sessionID`, `userID` if logged in)
 *
 * INTERVIEW POINT:
 * Automatic Body Transformation Interceptor avoids polluting DTO request models with repeated
 * boilerplate fields across every network API endpoint.
 */
class AutoBodyInterceptor(
    private val deviceInfoProvider: DeviceInfoProvider,
    private val tokenManager: TokenDataStoreManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        if (originalRequest.header("Content-Type") == null) {
            requestBuilder.header("Content-Type", "application/json")
        }
        if (originalRequest.header("Accept") == null) {
            requestBuilder.header("Accept", "application/json")
        }

        if (originalRequest.method == "POST" || originalRequest.method == "PUT") {
            val existingBody = originalRequest.body

            val newJsonString = try {
                val existingJson = if (existingBody != null) {
                    val buffer = Buffer()
                    existingBody.writeTo(buffer)
                    buffer.readUtf8().ifBlank { "{}" }
                } else {
                    "{}"
                }

                val jsonObject = if (existingJson.trim().startsWith("{")) {
                    JSONObject(existingJson)
                } else {
                    JSONObject()
                }

                // Automatically inject Latitude & Longitude into every API request
                jsonObject.put("Latitude", deviceInfoProvider.latitude)
                jsonObject.put("Longitude", deviceInfoProvider.longitude)

                // Inject Common System Parameters
                if (!jsonObject.has("appid")) jsonObject.put("appid", ApiEndpoints.APP_ID)
                if (!jsonObject.has("domain")) jsonObject.put("domain", ApiEndpoints.APP_DOMAIN)
                if (!jsonObject.has("version")) jsonObject.put("version", deviceInfoProvider.getAppVersion())
                if (!jsonObject.has("imei")) jsonObject.put("imei", deviceInfoProvider.getDeviceImei())
                if (!jsonObject.has("serialNo")) jsonObject.put("serialNo", deviceInfoProvider.getDeviceSerialNo())
                if (!jsonObject.has("regKey")) jsonObject.put("regKey", "")
                if (!jsonObject.has("loginTypeID")) jsonObject.put("loginTypeID", 1)

                // Inject Saved User Session Parameters if available
                val savedUser = tokenManager.getSavedUser()
                if (savedUser != null && savedUser.token.accessToken.isNotBlank()) {
                    if (!jsonObject.has("session")) jsonObject.put("session", savedUser.token.accessToken)
                    if (!jsonObject.has("sessionID")) {
                        val numericSessionId = savedUser.sessionID.toIntOrNull() ?: savedUser.sessionID.toLongOrNull()
                        if (numericSessionId != null) {
                            jsonObject.put("sessionID", numericSessionId)
                        } else {
                            jsonObject.put("sessionID", savedUser.sessionID)
                        }
                    }
                    if (!jsonObject.has("userID")) {
                        val numericUserId = savedUser.id.toIntOrNull()
                        if (numericUserId != null) {
                            jsonObject.put("userID", numericUserId)
                        } else {
                            jsonObject.put("userID", savedUser.id)
                        }
                    }
                }

                jsonObject.toString()
            } catch (e: Exception) {
                Log.e("AutoBodyInterceptor", "Error modifying JSON body: ${e.localizedMessage}")
                "{}"
            }

            val modifiedBody = newJsonString.toRequestBody("application/json".toMediaTypeOrNull())
            requestBuilder.method(originalRequest.method, modifiedBody)
        }

        val response = chain.proceed(requestBuilder.build())

        // Global Invalid Session & Session Expired Detection
        if (response.code == 401) {
            tokenManager.notifySessionExpired()
        } else {
            val responseBody = response.body
            try {
                val source = responseBody.source()
                source.request(Long.MAX_VALUE)
                val buffer = source.buffer
                val responseJson = buffer.clone().readUtf8()

                val lowerJson = responseJson.lowercase()
                if (lowerJson.contains("invalid session") ||
                    lowerJson.contains("invaild session") ||
                    lowerJson.contains("session expired") ||
                    lowerJson.contains("session invalid") ||
                    lowerJson.contains("session invaild") ||
                    lowerJson.contains("redirecttologin") ||
                    lowerJson.contains("session timeout")
                ) {
                    Log.w("AutoBodyInterceptor", "Invalid Session detected in API response: $responseJson")
                    tokenManager.notifySessionExpired()
                }
            } catch (e: Exception) {
                Log.e("AutoBodyInterceptor", "Error reading response body for session check: ${e.localizedMessage}")
            }
        }

        return response
    }
}
/*"password":"July@2026","regKey":"","serialNo":"8545424a0797e8aa","userID":"9851937038",*/
class KtorInterceptorConfig {
    var appVersion: String = "1.0.0"
    var defaultLanguage: String = "en"
}

/**
 * Ktor Custom Plugin Interceptor for custom header injection and HTTP status tracking.
 */
val KtorHeaderInterceptorPlugin = createClientPlugin("KtorHeaderInterceptorPlugin", ::KtorInterceptorConfig) {
    val appVersion = pluginConfig.appVersion
    val defaultLanguage = pluginConfig.defaultLanguage

    onRequest { request, _ ->
        request.header(HttpHeaders.UserAgent, "AuraGlass-Android/$appVersion")
        request.header(ApiEndpoints.Headers.ACCEPT_LANGUAGE, defaultLanguage)
        request.header(ApiEndpoints.Headers.X_APP_VERSION, appVersion)
        request.header(ApiEndpoints.Headers.X_PLATFORM, "Android-Compose")
    }
}

/**
 * Extension function to apply global interceptors and response status observers to Ktor HttpClient.
 */
fun HttpClientConfig<*>.applyGlobalInterceptors(
    appVersion: String = "1.0.0",
    languageCode: String = "en"
) {
    install(KtorHeaderInterceptorPlugin) {
        this.appVersion = appVersion
        this.defaultLanguage = languageCode
    }

    install(ResponseObserver) {
        onResponse { response ->
            println("[Ktor Response Observer] Endpoint: ${response.call.request.url} | Status: ${response.status.value}")
        }
    }
}
