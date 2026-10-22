package com.example.kotlinkmpsetup.domain.model

/**
 * ====================================================================================
 * KOTLIN / CLEAN ARCHITECTURE: UNIFIED GENERIC NETWORK RESULT WRAPPER
 * ====================================================================================
 * A sealed type-safe hierarchy representing every network request state:
 * - `Pending` / `Idle`: Initial un-triggered state.
 * - `Loading`: In-progress background execution.
 * - `Success`: Successful API response with generic type [T].
 * - `Error`: Exception or server error response.
 * - `NetworkOffline`: Device internet connectivity missing.
 *
 * INTERVIEW POINTS COVERED:
 * 1. **Sealed Interface vs Sealed Class**: Sealed interface permits value classes & zero-memory `data object` declarations.
 * 2. **Covariance (`out T`)**: Allows `NetworkResult<Nothing>` to be assigned to any `NetworkResult<T>`.
 * 3. **Inline Extension Functions**: Eliminates heap allocation for lambda objects during state checks.
 * 4. **Higher-Order Functions**: Enables declarative chained handling like `.onSuccess { ... }.onError { ... }`.
 */
sealed interface NetworkResult<out T> {

    /** Un-triggered or reset state */
    data object Pending : NetworkResult<Nothing>

    /** In-flight asynchronous network request */
    data object Loading : NetworkResult<Nothing>

    /** Successful response containing payload data of type [T] */
    data class Success<out T>(val data: T) : NetworkResult<T>

    /** Server error response, HTTP non-2xx status, or exception */
    data class Error(
        val message: String,
        val code: Int? = null,
        val cause: Throwable? = null
    ) : NetworkResult<Nothing>

    /** Device internet connection unavailable */
    data object NetworkOffline : NetworkResult<Nothing>
}

// ====================================================================================
// KOTLIN INLINE EXTENSION & HIGHER-ORDER FUNCTIONS
// ====================================================================================

/**
 * Executes [action] if the result is [NetworkResult.Success].
 * Uses `inline` to avoid runtime lambda object instantiation on the heap.
 */
inline fun <T> NetworkResult<T>.onSuccess(action: (data: T) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Success) {
        action(data)
    }
    return this
}

/**
 * Executes [action] if the result is [NetworkResult.Error].
 */
inline fun <T> NetworkResult<T>.onError(action: (message: String, code: Int?) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Error) {
        action(message, code)
    }
    return this
}

/**
 * Executes [action] if the result is [NetworkResult.Loading].
 */
inline fun <T> NetworkResult<T>.onLoading(action: () -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Loading) {
        action()
    }
    return this
}

/**
 * Executes [action] if the result is [NetworkResult.Pending].
 */
inline fun <T> NetworkResult<T>.onPending(action: () -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Pending) {
        action()
    }
    return this
}

/**
 * Transforms [NetworkResult.Success] payload using [transform] lambda.
 */
inline fun <T, R> NetworkResult<T>.map(transform: (T) -> R): NetworkResult<R> {
    return when (this) {
        is NetworkResult.Success -> NetworkResult.Success(transform(data))
        is NetworkResult.Error -> NetworkResult.Error(message, code, cause)
        is NetworkResult.Loading -> NetworkResult.Loading
        is NetworkResult.Pending -> NetworkResult.Pending
        is NetworkResult.NetworkOffline -> NetworkResult.NetworkOffline
    }
}

/**
 * Pattern-matches all states declaratively using lambdas with return value [R].
 */
inline fun <T, R> NetworkResult<T>.fold(
    onPending: () -> R,
    onLoading: () -> R,
    onSuccess: (data: T) -> R,
    onError: (message: String, code: Int?) -> R,
    onOffline: () -> R
): R {
    return when (this) {
        is NetworkResult.Pending -> onPending()
        is NetworkResult.Loading -> onLoading()
        is NetworkResult.Success -> onSuccess(data)
        is NetworkResult.Error -> onError(message, code)
        is NetworkResult.NetworkOffline -> onOffline()
    }
}

// ====================================================================================
// EXTENSION FUNCTIONS FOR LIST & COLLECTION HANDLING
// ====================================================================================

/**
 * Returns payload List or empty list if state is not [NetworkResult.Success].
 */
fun <T> NetworkResult<List<T>>.orEmptyList(): List<T> {
    return (this as? NetworkResult.Success)?.data ?: emptyList()
}

/**
 * Returns item count of payload List, or 0 if not successful.
 */
fun <T> NetworkResult<List<T>>.itemCount(): Int {
    return (this as? NetworkResult.Success)?.data?.size ?: 0
}

/**
 * Returns `true` if state is [NetworkResult.Loading].
 */
val <T> NetworkResult<T>.isLoading: Boolean
    get() = this is NetworkResult.Loading

/**
 * Returns `true` if state is [NetworkResult.Success].
 */
val <T> NetworkResult<T>.isSuccess: Boolean
    get() = this is NetworkResult.Success
