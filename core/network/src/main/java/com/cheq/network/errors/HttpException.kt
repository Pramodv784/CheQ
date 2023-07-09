package com.cheq.network.errors

import com.cheq.network.errors.ErrorConstants.UNKNOWN_MESSAGE
import java.io.IOException

/**
 * Created by Akash Khatkale on 1st June, 2023.
 * akash.k@cheq.one
 */
data class HttpException(
    val statusCode: Int,
    val headers: Map<String, List<String>>
) : IOException(UNKNOWN_MESSAGE)
