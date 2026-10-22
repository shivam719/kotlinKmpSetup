package com.example.kotlinkmpsetup.data.remote

import com.example.kotlinkmpsetup.domain.model.NetworkResult
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException

/**
 * ====================================================================================
 * KOTLIN / CLEAN ARCHITECTURE: SAFE API CALL & EXCEPTION INTERCEPTOR WRAPPER
 * ====================================================================================
 * Executes suspending network API calls safely, catching Ktor HTTP & SSL exceptions and
 * mapping them into [NetworkResult] states ([NetworkResult.Success], [NetworkResult.Error], [NetworkResult.NetworkOffline]).
 *
 * INTERVIEW POINTS COVERED:
 * 1. **`inline` with `reified`**: Reification keeps generic type [T] alive at runtime without Java type erasure.
 * 2. **`crossinline`**: Guarantees suspend lambda [apiCall] cannot execute non-local returns from calling scope.
 * 3. **Higher-Order Functions**: Takes suspending function block as parameter.
 * 4. **SSL & Network Interception**: Catches 4xx Client, 5xx Server, SSL Handshake, Timeout, and Offline Exceptions gracefully.
 */
suspend inline fun <reified T> safeApiCall(
    crossinline apiCall: suspend () -> T
): NetworkResult<T> {
    return try {
        val result = apiCall()
        NetworkResult.Success(result)
    } catch (e: ClientRequestException) {
        val statusCode = e.response.status.value
        val message = when (statusCode) {
            ApiEndpoints.HttpStatus.BAD_REQUEST -> "Bad request. Please check input parameters."
            ApiEndpoints.HttpStatus.UNAUTHORIZED -> "Unauthorized session. Please sign in again."
            ApiEndpoints.HttpStatus.FORBIDDEN -> "Access forbidden. You do not have permission."
            ApiEndpoints.HttpStatus.NOT_FOUND -> "Requested endpoint resource not found (404)."
            ApiEndpoints.HttpStatus.CONFLICT -> "Resource conflict. Already exists."
            ApiEndpoints.HttpStatus.UNPROCESSABLE_ENTITY -> "Validation error. Unprocessable input."
            else -> e.message
        }
        NetworkResult.Error(message = message, code = statusCode, cause = e)
    } catch (e: ServerResponseException) {
        NetworkResult.Error(
            message = "Server error (${e.response.status.value}). Please try again later.",
            code = e.response.status.value,
            cause = e
        )
    } catch (e: RedirectResponseException) {
        NetworkResult.Error(
            message = "Unexpected server redirect response.",
            code = e.response.status.value,
            cause = e
        )
    } catch (e: HttpRequestTimeoutException) {
        NetworkResult.Error(
            message = "Connection request timed out. Please check network speed.",
            cause = e
        )
    } catch (e: SocketTimeoutException) {
        NetworkResult.Error(
            message = "Socket connection timed out. Server taking too long to respond.",
            cause = e
        )
    } catch (e: SSLHandshakeException) {
        NetworkResult.Error(
            message = "SSL Certificate Handshake Failed: Untrusted or self-signed server SSL certificate.",
            cause = e
        )
    } catch (e: SSLException) {
        NetworkResult.Error(
            message = "SSL Connection Error: ${e.localizedMessage ?: "Secure connection could not be established."}",
            cause = e
        )
    } catch (e: UnknownHostException) {
        NetworkResult.NetworkOffline
    } catch (e: Exception) {
        NetworkResult.Error(
            message = e.localizedMessage ?: "An unexpected error occurred.",
            cause = e
        )
    }
}
