package com.cheq.profile.domain.repository

import com.cheq.profile.domain.entities.TransactionHistory
import com.cheq.profile.domain.entities.TransactionHistoryDetail

/**
 * Created by Akash Khatkale on 17th May, 2023.
 * akash.k@cheq.one
 */
interface TransactionHistoryRepository {

    suspend fun getTransactionHistory(): Result<TransactionHistory>

    suspend fun getTransactionDetails(
        billId: String,
        cheqUniTransactionId: String
    ): Result<TransactionHistoryDetail>

}