package com.cheq.profile.data.mappers

import com.cheq.common.extension.orZero
import com.cheq.profile.data.models.ReferralHistoryDto
import com.cheq.profile.domain.entities.ReferralHistory

/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
fun ReferralHistoryDto.toReferralHistoryModel() =
    ReferralHistory(
        httpStatus = this.httpStatus.orZero,
        history = this.history?.map {
            ReferralHistory.HistoryItem(
                name = it?.name.orEmpty(),
                creationDate = it?.creationDate.orEmpty(),
                status = ReferralHistory.HistoryItem.HistoryItemStatus.find(it?.status.orEmpty()),
                message = it?.message?.map { m ->
                    m.orEmpty()
                } ?: emptyList(),
                type = ReferralHistory.HistoryItem.HistoryItemType.find(it?.type.orEmpty())
            )
        } ?: emptyList()
    )