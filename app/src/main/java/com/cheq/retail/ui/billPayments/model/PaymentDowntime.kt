package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep
import com.google.firebase.database.PropertyName

@Keep
data class PaymentDowntime(

    @set:PropertyName("DC_Flag")
    @get:PropertyName("DC_Flag")
    var dcFlag: Boolean? = true,

    @set:PropertyName("NB_Flag")
    @get:PropertyName("NB_Flag")
    var nbFlag: Boolean? = true,

    @set:PropertyName("UPI_Flag")
    @get:PropertyName("UPI_Flag")
    var upiFlag: Boolean? = true,

    @set:PropertyName("Flag")
    @get:PropertyName("Flag")
    var flag: Boolean? = false,

    @set:PropertyName("URL")
    @get:PropertyName("URL")
    var url: String? = ""
)