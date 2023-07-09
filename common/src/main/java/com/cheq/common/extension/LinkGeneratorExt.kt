package com.cheq.common.extension

import android.content.Context
import com.appsflyer.share.LinkGenerator
import com.cheq.common.utils.Constants.DOMAIN
import com.cheq.common.utils.Constants.REFERRAL_IMAGE_URL
import com.cheq.common.utils.DeeplinkConstants.AF_OG_DESCRIPTION
import com.cheq.common.utils.DeeplinkConstants.AF_OG_IMAGE
import com.cheq.common.utils.DeeplinkConstants.AF_OG_TITLE
import com.cheq.common.utils.DeeplinkConstants.DEEP_LINK_SUB1
import com.cheq.common.utils.DeeplinkConstants.DEEP_LINK_SUB2
import com.cheq.common.utils.DeeplinkConstants.DEEP_LINK_VALUE
import com.cheq.common.utils.DeeplinkConstants.INVITE

/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
fun LinkGenerator.getInviteHelper(
    cheqUserId: String,
    context: Context,
    title: String,
    description: String,
) {
    this.addParameter(DEEP_LINK_VALUE, cheqUserId)
    this.brandDomain = DOMAIN
    this.addParameter(DEEP_LINK_SUB1, INVITE)
    this.addParameter(DEEP_LINK_SUB2, cheqUserId)
    this.addParameter(
        AF_OG_IMAGE,
        REFERRAL_IMAGE_URL
    )
    this.addParameter(AF_OG_TITLE, title)
    this.addParameter(AF_OG_DESCRIPTION, description)
}