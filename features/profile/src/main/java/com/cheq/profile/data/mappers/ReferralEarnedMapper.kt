package com.cheq.profile.data.mappers

import com.cheq.common.extension.orFalse
import com.cheq.common.extension.orZero
import com.cheq.profile.data.models.ReferralEarnedDto
import com.cheq.profile.domain.entities.ReferralEarned


/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
fun ReferralEarnedDto.toReferralEarnedModel() =
    ReferralEarned(
        show = this.show.orFalse,
        totalRewardsEarned = this.totalRewardsEarned.orZero,
        httpStatus = this.httpStatus.orZero,
        totalReferralLimit = this.totalReferralLimit.orZero,
        totalFriendsInvited = this.totalFriendsInvited.orZero
    )