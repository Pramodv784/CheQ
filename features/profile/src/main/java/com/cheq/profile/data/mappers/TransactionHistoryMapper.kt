package com.cheq.profile.data.mappers

import com.cheq.common.extension.orFalse
import com.cheq.common.extension.orZero
import com.cheq.profile.data.models.TransactionHistoryDto
import com.cheq.profile.domain.entities.TransactionHistory

/**
 * Created by Akash Khatkale on 16th May, 2023.
 * akash.k@cheq.one
 */
fun TransactionHistoryDto.toTransactionHistoryModel(): TransactionHistory {
    return TransactionHistory(
        transactionList = transactionList?.map {
            TransactionHistory.TransactionHistoryItem(
                cashAmount = it?.cashAmount.orZero,
                payingStatus = it?.payingStatus.orEmpty(),
                payingFailureReason = it?.payingFailureReason,
                createdAt = it?.createdAt.orEmpty(),
                rewardTransactionId = it?.rewardTransactionId,
                updatedAt = it?.updatedAt.orEmpty(),
                productId = it?.productId.orEmpty(),
                id = it?.id.orEmpty(),
                bankMasterId = it?.bankMasterId,
                payingTransactionId = it?.payingTransactionId,
                payoutTransactionId = it?.payoutTransactionId,
                paymentMethod = it?.paymentMethod,
                cheqUniTransactionId = it?.cheqUniTransactionId.orEmpty(),
                billId = it?.billId.orEmpty(),
                tender = it?.tender,
                billAmount = it?.billAmount.orZero,
                billTotalAmount = it?.billTotalAmount.orZero,
                paidTogether = it?.paidTogether.orFalse,
                createdBy = it?.createdBy.orEmpty(),
                chipAmount = it?.chipAmount.orZero,
                rewardRefId = it?.rewardRefId,
                systemGenerated = it?.systemGenerated.orFalse,
                payoutFailureReason = it?.payoutFailureReason,
                payoutStatus = it?.payoutStatus,
                cheqUserId = it?.cheqUserId.orEmpty(),
                narration = it?.narration.orEmpty(),
                updatedBy = it?.updatedBy.orEmpty(),
                comment = it?.comment,
                status = it?.status.orEmpty(),
                chipUsed = it?.chipUsed.orZero,
            )
        }?.toList() ?: emptyList()
    )
}