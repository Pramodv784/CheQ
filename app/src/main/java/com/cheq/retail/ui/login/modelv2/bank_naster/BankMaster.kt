package com.cheq.retail.ui.login.modelv2.bank_naster

import androidx.annotation.Keep

@Keep
data class BankMaster(
    val IFSC_Prefix: String,
    val __v: Int,
    val alias: List<String>,
    val bankName: String,
    val bureauBankName: String,
    val createdAt: String,
    val innerGridGradientColors: InnerGridGradientColors,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val logo: String,
    val logoWithName: String,
    val ocrBankName: String,
    val originalBankName: String,
    val outerGridGradientColors: OuterGridGradientColors,
    val rank: Int,
    val updatedAt: String
)