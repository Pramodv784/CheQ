package com.cheq.retail.ui.billSummary.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class BillSummaryRequest(
	val billAmount: String? = null,
	val paymentMode: String? = null,
	val paymentModeInstrument: PaymentModeInstrument? = null,
	val chipQuantity: Int? = null,
	val payTogether: Boolean? = null,
	val products: List<ProductItem?>? = null,
    val chipsUsed: Boolean? = null
)

@Keep
data class DcBin(
	val iin: String? = null,
	val entity: String? = null,
	val network: String? = null,
	val type: String? = null,
	val issuer_code: String? = null,
	val issuer_name: String? = null,

)
@Keep
data class ProductItem(
	val amount: String? = null,
	val id: String? = null
)
@Keep
data class PaymentModeInstrument(

	val upiPackageName: String? = null,
	val nbBankmasterId: String? = null,
	val nbBankmaster: String? = null,
	val dcNetwork: String? = null,
	val dcBin: DcBin? = null,
	val dcToken: String? = null,
	val dcBankmasterId: String? = null
)
