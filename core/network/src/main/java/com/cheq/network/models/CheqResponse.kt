package com.cheq.network.models

/**
 * Created by Akash Khatkale on 5th June, 2023.
 * akash.k@cheq.one
 */
data class CheqResponse(
    var data: String?,
    val error: Any?,
    val httpStatus: Int?,
    val message: String?,
    val status: Boolean?,
    val traceId: String?
)