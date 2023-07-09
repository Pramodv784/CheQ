package com.cheq.retail.ui.dashboard.model.homedashboad

import androidx.annotation.Keep

@Keep
data class Address(
    val address: String,
    val city: String,
    val state: String
)