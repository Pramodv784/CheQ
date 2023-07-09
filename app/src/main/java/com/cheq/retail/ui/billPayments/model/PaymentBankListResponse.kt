package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep

@Keep
data class PaymentBankListResponse(
    var data: DataEntity?,
    var message: String?,
    var status: Boolean?
) {
    @Keep
    data class DataEntity(
        var banks: List<BanksEntity>,
        var topSix: List<TopSix>
    )
    @Keep
    data class BanksEntity(
        var _id: String?,
        var logo: String?,
        var originalBankName: String?,
        var IFSC_Prefix: String?
    )
    @Keep
    data class TopSix(
        var logo: String?,
        var originalBankName: String?,
        var IFSC_Prefix: String?
    )
}