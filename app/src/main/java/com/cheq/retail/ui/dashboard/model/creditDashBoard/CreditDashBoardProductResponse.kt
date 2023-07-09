package com.cheq.retail.ui.dashboard.model.creditDashBoard

import androidx.annotation.Keep


@Keep
data class CreditDashBoardProductResponse (val data: Data2?,
                                           val error: Any,
                                           val httpStatus: Int,
                                           val message: String,
                                           val response: Any,
                                           val status: Boolean)