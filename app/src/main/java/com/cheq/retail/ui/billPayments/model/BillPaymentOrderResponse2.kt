package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class BillPaymentOrderResponse2(
    val TxnDetails: String,
    val cxDetails: CxDetails,
    val orderdetails: Orderdetails,
    val rzpCustomerId: String,
    val status: Boolean
): Serializable