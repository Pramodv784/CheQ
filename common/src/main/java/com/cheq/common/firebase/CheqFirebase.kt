package com.cheq.common.firebase


/**
 * Created by Akash Khatkale on 26th May, 2023.
 * akash.k@cheq.one
 */
interface CheqFirebase {
    suspend fun <T> getValue(
        reference: String,
        classOf: Class<T>
    ): Result<T?>
}