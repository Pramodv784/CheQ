package com.cheq.network.response

import com.cheq.network.errors.HttpException
import com.cheq.network.errors.NoInternetConnectionException
import com.cheq.network.errors.UnknownException
import com.google.gson.Gson
import java.lang.Exception

/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
sealed class NetworkResponse<out T> {
    data class Success<T>(
        val statusCode: Int,
        val headers: Map<String, List<String>>,
        val value: T
    ) : NetworkResponse<T>()

    sealed class Failure(val cause: Throwable) : NetworkResponse<Nothing>() {
        data class Http(
            val statusCode: Int,
            val headers: Map<String, List<String>>,
            val errorBody: String
        ) : Failure(HttpException(statusCode, headers))

        data class IO(
            val exception: Throwable
        ) : Failure(exception)

        data class Unknown(
            val exception: Throwable
        ) : Failure(exception)
    }
}

internal fun <T> NetworkResponse<T>.getOrThrow(): T = when (this) {
    is NetworkResponse.Success -> value
    is NetworkResponse.Failure.Http -> throw HttpException(statusCode, headers)
    is NetworkResponse.Failure.IO -> throw NoInternetConnectionException(exception)
    is NetworkResponse.Failure.Unknown -> throw UnknownException(exception)
}

fun <T> NetworkResponse<T>.toResult(): Result<T> = Result.runCatching { getOrThrow() }

fun <T, R> NetworkResponse<T>.map(mapper: T.() -> R): NetworkResponse<R> = when (this) {
    is NetworkResponse.Success -> NetworkResponse.Success(statusCode, headers, mapper(value))
    is NetworkResponse.Failure -> this
}

fun <T> toModel(value: String?, classOf: Class<T>): T? {
    return try {
        Gson().fromJson(value, classOf)
    } catch (e: Exception) {
        null
    }
}