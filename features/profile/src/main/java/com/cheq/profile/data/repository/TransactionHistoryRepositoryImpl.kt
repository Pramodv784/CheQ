package com.cheq.profile.data.repository

import com.cheq.network.response.map
import com.cheq.network.response.toResult
import com.cheq.profile.data.api.TransactionHistoryApi
import com.cheq.profile.data.mappers.toTransactionHistoryDetailModel
import com.cheq.profile.data.mappers.toTransactionHistoryModel
import com.cheq.profile.data.models.TransactionHistoryDetailRequest
import com.cheq.profile.domain.entities.TransactionHistory
import com.cheq.profile.domain.entities.TransactionHistoryDetail
import com.cheq.profile.domain.repository.TransactionHistoryRepository
import javax.inject.Inject

/**
 * Created by Akash Khatkale on 16th May, 2023.
 * akash.k@cheq.one
 */
class TransactionHistoryRepositoryImpl @Inject constructor(
    private val api: TransactionHistoryApi
) : TransactionHistoryRepository {
    override suspend fun getTransactionHistory(): Result<TransactionHistory> {
        return api
            .getTransactionHistory()
            .map { toTransactionHistoryModel() }
            .toResult()
    }

    override suspend fun getTransactionDetails(
        billId: String,
        cheqUniTransactionId: String
    ): Result<TransactionHistoryDetail> {
        return api
            .getTransactionDetails(TransactionHistoryDetailRequest(billId, cheqUniTransactionId))
            .map { toTransactionHistoryDetailModel() }
            .toResult()
    }
}
