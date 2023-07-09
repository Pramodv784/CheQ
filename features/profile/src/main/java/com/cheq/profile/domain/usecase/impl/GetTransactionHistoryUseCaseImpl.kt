package com.cheq.profile.domain.usecase.impl

import com.cheq.profile.domain.entities.TransactionHistory
import com.cheq.profile.domain.repository.TransactionHistoryRepository
import com.cheq.profile.domain.usecase.GetTransactionHistoryUseCase
import javax.inject.Inject

/**
 * Created by Akash Khatkale on 16th May, 2023.
 * akash.k@cheq.one
 */
class GetTransactionHistoryUseCaseImpl @Inject constructor(
    private val repository: TransactionHistoryRepository
) : GetTransactionHistoryUseCase {
    override suspend fun invoke(): Result<TransactionHistory> {
        return repository.getTransactionHistory()
    }
}