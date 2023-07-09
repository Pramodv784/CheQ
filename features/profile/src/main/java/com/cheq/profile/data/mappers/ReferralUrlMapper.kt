package com.cheq.profile.data.mappers

import com.cheq.common.extension.orFalse
import com.cheq.common.extension.orZero
import com.cheq.profile.data.models.ReferralUrlDto
import com.cheq.profile.domain.entities.ReferralUrl


/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
fun ReferralUrlDto.toReferralUrlModel() =
    ReferralUrl(
        referralUrl = this.referralUrl.orEmpty(),
        upsertedId = this.upsertedId.orZero,
        upsertedCount = this.upsertedCount.orZero,
        acknowledged = this.acknowledged.orFalse,
        modifiedCount = this.modifiedCount.orZero,
        matchedCount = this.matchedCount.orZero
    )