package com.cheq.retail.ui.activateCard.model

import androidx.annotation.Keep

@Keep
data class GenerateOrderIdResponse(
    var created_at: Int,
    var notes: List<String>?,
    var attempts: Int,
    var status: String?,
    var receipt: String?,
    var currency: String?,
    var amount_due: Int,
    var amount_paid: Int,
    var amount: Int,
    var entity: String?,
    var id: String?,
    var apiMessage: String,
    var customerData: CustomerData
) {
    @Keep
    data class CustomerData(
        var id: String?,
        var entity: String?,
        var name: String?,
        var email: String?,
        var contact: String?,
    )
}