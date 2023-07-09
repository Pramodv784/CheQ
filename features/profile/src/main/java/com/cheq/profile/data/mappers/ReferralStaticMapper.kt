package com.cheq.profile.data.mappers

import com.cheq.common.extension.orFalse
import com.cheq.common.extension.orZero
import com.cheq.profile.data.models.ReferralStaticDto
import com.cheq.profile.domain.entities.ReferralStatic

/**
 * Created by Akash Khatkale on 20th May, 2023.
 * akash.k@cheq.one
 */
fun ReferralStaticDto.toReferralStaticModel(): ReferralStatic =
    ReferralStatic(
        offerValid = this.offerValid.orFalse,
        youGetValue = this.youGetValue.orZero,
        youGetMessage = this.youGetMessage.orEmpty(),
        daysLeft = this.daysLeft.orEmpty(),
        defaultMessage = this.defaultMessage.orEmpty(),
        friendsGetMessage = this.friendsGetMessage.orEmpty(),
        friendsGetValue = this.friendsGetValue.orZero,
        whatsappMessage = this.whatsappMessage.orEmpty(),
        chips = this.chips.orZero,
        httpStatus = this.httpStatus.orZero,
        steps = this.steps?.map {
            it.orEmpty()
        } ?: emptyList(),
        type = ReferralStatic.ReferralStaticType.find(this.type.orEmpty())
    )