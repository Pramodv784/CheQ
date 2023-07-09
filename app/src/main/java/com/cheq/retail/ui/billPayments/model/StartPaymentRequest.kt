package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep

@Keep
data class StartPaymentRequest(
    var productId: String,
    var billId: String,
    var amount: String,
    var paymentMode: String,
    var number: String,
    var cvv: String,
    var expiry_month: String,
    var expiry_year: String,
    var name: String,
    var debit_card_network: String,
    var debit_card_bank_name: String,
    var upiPackageName: String,
    var netBankingObject: NetBankingRequest?,
    var bankMasterId:String
)