package com.cheq.profile.domain.usecase

import com.cheq.common.usecase.InputUseCase
import com.cheq.profile.domain.entities.TransactionHistoryDetail

/**
 * Created by Akash Khatkale on 17th May, 2023.
 * akash.k@cheq.one
 */
interface GetTransactionHistoryDetailsUseCase :
    InputUseCase<GetTransactionHistoryDetailsUseCase.GetTransactionHistoryDetailsInput, Result<TransactionHistoryDetail>> {
    data class GetTransactionHistoryDetailsInput(
        val billId: String,
        val cheqUniTransactionId: String
    )
}