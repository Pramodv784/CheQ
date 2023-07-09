package com.cheq.retail.ui.emandate.model

import androidx.annotation.Keep

@Keep
data class EmandateOrderResponse(
    var id: String?,
    var entity: String?,
    var amount: Int?,
    var amount_paid: Int?,
    var amount_due: Int?,
    var currency: String?,
    var receipt: String?,
    var offer_id: Int?,
    var status: String?,
    var attempts: Int?,
    var rzpCustomerId: String?,
    var apiMessage: String?,
    var token: TokenEntity?
) {
    @Keep
    data class TokenEntity(var bank_account: BankAccount?)
    @Keep
    data class BankAccount(
        var beneficiary_email: String?
    )
}