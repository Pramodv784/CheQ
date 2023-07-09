package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep

@Keep
data class TransectionId(  var UPI: String,var razorpay_payment_id: String,var txn_id: String)
