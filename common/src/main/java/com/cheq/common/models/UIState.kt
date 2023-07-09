package com.cheq.common.models

/**
 * Created by Akash Khatkale on 4th June, 2023.
 * akash.k@cheq.one
 */
sealed class UIState<T> {
    class Loading<T> : UIState<T>()
    data class Success<T>(val data: T) : UIState<T>()
    data class Error<T>(val error: Throwable) : UIState<T>()
}

fun <T> UIState<T>.takeIfLoading(onLoading: UIState.Loading<T>.() -> Unit): UIState<T> {
    return when (this) {
        is UIState.Loading -> {
            onLoading(this)
            this
        }

        else -> {
            this
        }
    }
}

fun <T> UIState<T>.takeIfSuccess(onSuccess: UIState.Success<T>.() -> Unit): UIState<T> {
    return when (this) {
        is UIState.Success -> {
            onSuccess(this)
            this
        }

        else -> {
            this
        }
    }
}

fun <T> UIState<T>.takeIfError(onError: UIState.Error<T>.() -> Unit): UIState<T> {
    return when (this) {
        is UIState.Error -> {
            onError(this)
            this
        }

        else -> {
            this
        }
    }
}