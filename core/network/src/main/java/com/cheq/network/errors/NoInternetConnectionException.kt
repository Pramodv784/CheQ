package com.cheq.network.errors

import com.cheq.network.errors.ErrorConstants.NO_INTERNET_CONNECTION_MESSAGE
import java.io.IOException

/**
 * Created by Akash Khatkale on 1st June, 2023.
 * akash.k@cheq.one
 */
data class NoInternetConnectionException(
    val exception: Throwable
) : IOException(NO_INTERNET_CONNECTION_MESSAGE)
