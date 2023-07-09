package com.cheq.profile.domain.usecase.impl

import com.cheq.profile.domain.entities.TransactionHistoryDetail
import com.cheq.profile.domain.repository.TransactionHistoryRepository
import com.cheq.profile.domain.usecase.GetTransactionHistoryDetailsUseCase
import javax.inject.Inject

/**
 * Created by Akash Khatkale on 16th May, 2023.
 * akash.k@cheq.one
 */
class GetTransactionHistoryDetailsUseCaseImpl @Inject constructor(
    private val repository: TransactionHistoryRepository
) : GetTransactionHistoryDetailsUseCase {

    override suspend fun invoke(input: GetTransactionHistoryDetailsUseCase.GetTransactionHistoryDetailsInput): Result<TransactionHistoryDetail> {
        return repository.getTransactionDetails(
            input.billId,
            input.cheqUniTransactionId
        )
    }
}