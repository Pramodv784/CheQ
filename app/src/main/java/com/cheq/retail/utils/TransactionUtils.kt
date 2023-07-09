package com.cheq.retail.utils

import com.cheq.retail.constants.AFConstants
import com.cheq.retail.constants.AFConstants.THIRTY_MINS
import com.cheq.retail.constants.AFConstants.TWO_DAYS
import com.cheq.retail.constants.AFConstants.TWO_MINS

object TransactionUtils {

    fun estimatedTime(type: String?, mode: String?): String? {
        if (type == AFConstants.CC && mode == AFConstants.IMPS) {
            return TWO_MINS
        } else if (type == AFConstants.CC && mode == AFConstants.NEFT) {
            return THIRTY_MINS
        } else if (type == AFConstants.LOAN) {
            return TWO_DAYS
        }
        return TWO_MINS
    }
}