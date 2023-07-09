package com.cheq.network.errors

import java.lang.Exception

/**
 * Created by Akash Khatkale on 1st June, 2023.
 * akash.k@cheq.one
 */
data class UnknownException(
    val exception: Throwable
) : Exception("Please try again later")
