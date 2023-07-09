package com.cheq.retail.ui.login.modelv2.productv1

import androidx.annotation.Keep
import java.io.Serializable
@Keep
data class BankMasterRecord(
    val IFSC_Prefix: String,
    val __v: Int,
    val id: String,
    val alias: List<String>,
    val bankName: String,
    val billerName: String,
    val bureauBankName: String,
    val createdAt: String,
    val innerGridGradientColors: InnerGridGradientColorsX,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val logo: String,
    val logoWithName: String,
    val ocrBankName: String,
    val originalBankName: String,
    val ui_config: UI,
    val outerGridGradientColors: OuterGridGradientColorsX,
    val rank: Int,
    val updatedAt: String,
    val paymentAmountExactness : String
    ) : Serializable