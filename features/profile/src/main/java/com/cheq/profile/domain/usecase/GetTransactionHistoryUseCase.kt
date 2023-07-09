package com.cheq.profile.domain.usecase

import com.cheq.common.usecase.NoInputUseCase
import com.cheq.profile.domain.entities.TransactionHistory

/**
 * Created by Akash Khatkale on 16th May, 2023.
 * akash.k@cheq.one
 */
interface GetTransactionHistoryUseCase: NoInputUseCase<Result<TransactionHistory>>