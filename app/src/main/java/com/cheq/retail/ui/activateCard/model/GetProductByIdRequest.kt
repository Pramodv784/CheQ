package com.cheq.retail.ui.activateCard.model

import androidx.annotation.Keep

@Keep
data class GetProductByIdRequest(
    val product_id: String,
    val txn_id: String
)