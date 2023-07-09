package com.cheq.retail.ui.dashboard.model.homedashboad

import androidx.annotation.Keep
import com.cheq.retail.ui.login.modelv2.productv1.ProductV2
@Keep
data class BalanceDueWidget (
    val balanceDue: Int?,
    var products: ArrayList<ProductV2>

): java.io.Serializable