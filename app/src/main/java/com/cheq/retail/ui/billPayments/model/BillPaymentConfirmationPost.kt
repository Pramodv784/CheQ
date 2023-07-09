package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep
import com.google.gson.JsonObject
import org.json.JSONObject
@Keep
data class BillPaymentConfirmationPost(
    var amount: Double,
    var razorpay_order_id: String,
    var billId: String,
    var razorpay_payment_id: String,
    var razorpay_signature: String,
    var productId: String,
    var txn_id: String,
    var bankMasterId: String,
    var paymentMode: String,
    var DebitLastFour: String,
    var upiPackageName: String,
    var netBankingObject: NetBankingRequest?
)