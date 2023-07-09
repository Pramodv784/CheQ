package com.cheq.retail.ui.activateCard.model

import androidx.annotation.Keep

@Keep
data class PostNewCardDetails(
    var international: Boolean,
    var issuer_logo: String?,
    var issuer_name: String?,
    var issuer_code: String?,
    var sub_type: String?,
    var type: String?,
    var network: String?,
    var razorpay_signature: String?,
    var razorpay_order_id: String?,
    var razorpay_payment_id: String?,
    var exp: String?,
    var cardNumber: Long = 0,
    var productId: String,
    var cardHolderName: String,
    var bankMasterId: String
)