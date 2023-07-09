package com.cheq.retail.ui.main.model

import androidx.annotation.Keep

@Keep
data class RazorpayCardDetailsModel(
    var authentication_types: List<AuthenticationTypesEntity>,
    var recurring: RecurringEntity? = null,
    var emi: EmiEntity,
    var international: Boolean,
    var issuer_name: String,
    var issuer_code: String,
    var sub_type: String,
    var type: String,
    var network: String,
    var entity: String,
    var iin: String
) {
    @Keep
    data class AuthenticationTypesEntity(
        var type: String
    )
    @Keep
    class RecurringEntity(
        var available: Boolean
    )
    @Keep
    class EmiEntity(
        var available: Boolean
    )
}