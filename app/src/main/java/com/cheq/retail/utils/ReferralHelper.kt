package com.cheq.retail.utils

import android.content.Context
import com.appsflyer.share.LinkGenerator
import com.cheq.retail.R
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.constants.AFConstants.AF_OG_DESCRIPTION
import com.cheq.retail.constants.AFConstants.AF_OG_IMAGE
import com.cheq.retail.constants.AFConstants.AF_OG_TITLE
import com.cheq.retail.constants.AFConstants.DEEP_LINK_SUB1
import com.cheq.retail.constants.AFConstants.DEEP_LINK_SUB2
import com.cheq.retail.constants.AFConstants.DEEP_LINK_VALUE
import com.cheq.retail.constants.AFConstants.INVITE

object ReferralHelper {

    fun LinkGenerator.getInviteHelper(cheqUserId: String, context: Context) {
        this.addParameter(DEEP_LINK_VALUE, cheqUserId)
        this.brandDomain = AFConstants.AF_DOMAIN
        this.addParameter(DEEP_LINK_SUB1, INVITE)
        this.addParameter(DEEP_LINK_SUB2, cheqUserId)
        this.addParameter(
            AF_OG_IMAGE,
            "https://firebasestorage.googleapis.com/v0/b/cheqmobileapp.appspot.com/o/Referral.png?alt=media&token=25d4b969-fd57-493e-8f68-d86fc9fa522f"
        )
        this.addParameter(AF_OG_TITLE, context.getString(R.string.af_og_title))
        this.addParameter(AF_OG_DESCRIPTION, context.getString(R.string.af_og_description))
    }
}