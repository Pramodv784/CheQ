package com.cheq.retail.utils

sealed class Resource<T> {
    data class Loaded<T>(val value: T) : Resource<T>()

    data class Error<T>(val throwable: Throwable) : Resource<T>()

    data class Pending<T>(val value: T?) : Resource<T>()

    data class Initial<T>(val value: T?) : Resource<T>()
}
