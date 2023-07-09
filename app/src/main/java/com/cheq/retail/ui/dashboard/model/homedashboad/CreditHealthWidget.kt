package com.cheq.retail.ui.dashboard.model.homedashboad

import androidx.annotation.Keep

@Keep
data class CreditHealthWidget(
    val __v: Int,
    val address: Address,
    val age: Int,
    val bureauProvider: String?,
    val ccUtilizationAvgInterpretation: String?,
    val ccUtilizationAvgValue: Double?,
    val cheqUserId: String,
    val createdAt: String,
    val creditAgeInterpretation: String?,
    val creditAgeValue: Int?,
    val creditEnquiriesInterpretation: Any,
    val creditEnquiriesValue: Int?,
    val creditScoreAgeTemplate: String?,
    val creditScoreAgeValue: String?,
    val creditScoreAgeValueInterpretation: String?,
    val creditScoreAgeValueTop: Boolean?,
    val creditScoreColorValue: String?,
    val creditScoreGraph: List<CreditScoreGraph>?,
    val creditScoreInterpretation: String?,
    val creditScoreNationalTemplate: String?,
    val creditScoreNationalValue: String?,
    val creditScoreNationalValueInterpretation: String?,
    val creditScoreNationalValueTop: Boolean?,
    var creditScoreStateTemplate: String?,
    val creditScoreStateValue: String?,
    val creditScoreStateValueInterpretation: String?,
    val creditScoreStateValueTop: Boolean?,
    val creditScoreValue: Int?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val paymentHistoryCount: Int?,
    val paymentHistoryInterpretation: String?,
    val paymentHistoryMissedCount: Int?,
    val paymentHistoryPaidCount: Int?,
    val paymentHistoryValue: Double?,
    val products: ArrayList<CreditDashBoardProductModel?>,
    val updatedAt: String
)
@Keep
class  CreditDashBoardProductModel(  val instrument_master_name: String?,
                                     val instrument_master_id: String?,
                                     val instrument_type: String?,
                                   //  val rawProduct: String,
                                     )